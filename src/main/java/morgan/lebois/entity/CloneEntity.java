package morgan.lebois.entity;

import morgan.lebois.powers.WingsPowerType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class CloneEntity extends PathAwareEntity implements Tameable {
    protected static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(CloneEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    protected static final TrackedData<Boolean> SITTING = DataTracker.registerData(CloneEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public CloneEntity(EntityType<? extends CloneEntity> entityType, World world) {
        super(entityType, world);
    }

    public CloneEntity(World world, LivingEntity owner) {
        super(LeboisEntityType.CLONE, world);
        this.setOwnerUuid(owner.getUuid());
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.35F, false));
        this.goalSelector.add(2, new FollowOwnerGoal(this));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 16f));
        this.goalSelector.add(5, new LookAtEntityGoal(this, AnimalEntity.class, 16f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(0, new DefendOwnerGoal(this));
        this.targetSelector.add(1, new FightForOwnerGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0F)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35f)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 4.0F)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64);
    }

    @Override
    public @Nullable UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    public void setSitting(boolean sitting) {
        this.dataTracker.set(SITTING, sitting);
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        return !this.isOwner(target) && !(target instanceof Tameable tameable && this.isOwner(tameable.getOwnerUuid())) && super.canTarget(target);
    }

    public boolean isOwner(UUID uuid) {
        return Objects.equals(this.getOwnerUuid(), uuid);
    }

    public boolean isOwner(LivingEntity entity) {
        return entity == this.getOwner();
    }

    @Override
    public Team getScoreboardTeam() {
        LivingEntity livingEntity = this.getOwner();
        if (livingEntity != null) {
            return livingEntity.getScoreboardTeam();
        }

        return super.getScoreboardTeam();
    }

    @Override
    public boolean isTeammate(Entity other) {
        LivingEntity livingEntity = this.getOwner();
        if (other == livingEntity) {
            return true;
        }

        if (livingEntity != null) {
            return livingEntity.isTeammate(other);
        }

        return super.isTeammate(other);
    }

    @Override
    public void tick() {
        if (this.getOwnerUuid() == null) {
            this.discard();
            return;
        }

        if (WingsPowerType.hasWings(this)) {
            if (!(this.moveControl instanceof FlightMoveControl)) {
                this.moveControl = new FlightMoveControl(this, 20, false);
                this.navigation = new BirdNavigation(this, this.getWorld());
            }
        }
        else if (this.moveControl instanceof FlightMoveControl){
            this.moveControl = new MoveControl(this);
            this.navigation = new MobNavigation(this, this.getWorld());
        }

        if (this.getHealth() < this.getMaxHealth() && this.age % 20 == 0) {
            this.heal(1.0F);
        }

        super.tick();
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ActionResult actionResult = super.interactMob(player, hand);
        ItemStack itemStack = player.getStackInHand(hand);

        if (!actionResult.isAccepted()) {
            if (!itemStack.isEmpty()) {
                EquipmentSlot equipmentSlot = this.getPreferredEquipmentSlot(itemStack);
                this.equipStack(equipmentSlot, itemStack.copy());
                return ActionResult.success(this.getWorld().isClient());
            }
            else if (player == this.getOwner()) {
                this.setSitting(!this.isSitting());
                return ActionResult.success(this.getWorld().isClient());
            }
        }

        return actionResult;
    }

    @Override
    protected void dropEquipment(ServerWorld world, DamageSource source, boolean causedByPlayer) {
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean damaged = super.damage(source, amount);

        if (damaged && this.isSitting()) {
            this.setSitting(false);
        }

        return damaged;
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
        if (WingsPowerType.hasWings(this)) return;

        super.fall(heightDifference, onGround, state, landedPosition);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SITTING, false);
        builder.add(OWNER_UUID, Optional.empty());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("owner", this.getOwnerUuid());
        }

        nbt.putBoolean("sitting", this.isSitting());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.containsUuid("owner")) {
            UUID uuid = nbt.getUuid("owner");
            if (uuid != null) {
                this.setOwnerUuid(uuid);
            }
        }

        this.setSitting(nbt.getBoolean("sitting"));
    }


    // Copied from https://github.com/0vergrown/Sync/blob/main/src/main/java/dev/overgrown/sync/factory/action/entity/summons/entities/clone/CloneEntity.java
    protected abstract static class CloneGoal extends Goal {
        protected final CloneEntity clone;
        protected LivingEntity owner;

        protected CloneGoal (CloneEntity clone) {
            this.clone = clone;
        }

        @Override
        public boolean canStart() {
            if (this.owner == null) owner = this.clone.getOwner();
            return !this.clone.isSitting() && this.owner != null;
        }
    }

    protected abstract static class AssistOwnerGoal extends TrackTargetGoal {
        protected final CloneEntity clone;
        protected LivingEntity owner;
        protected int timer;

        protected AssistOwnerGoal (CloneEntity clone) {
            super(clone, false);
            this.timer = 0;
            this.clone = clone;
            this.setControls(EnumSet.of(Goal.Control.TARGET));
        }

        @Override
        public boolean canStart () {
            if (owner == null) owner = this.clone.getOwner();
            return owner != null && !this.clone.isSitting();
        }
    }

    protected static class DefendOwnerGoal extends AssistOwnerGoal {
        public DefendOwnerGoal (CloneEntity clone) {
            super(clone);
        }

        @Override
        public boolean canStart () {
            if (super.canStart() && this.owner.getAttacker() != null && this.clone.canTarget(this.owner.getAttacker()) && this.timer != this.owner.getLastAttackedTime()) {
                this.target = this.owner.getAttacker();
                this.timer = this.owner.getLastAttackedTime();
                return true;
            }
            return false;
        }
    }

    protected static class FightForOwnerGoal extends AssistOwnerGoal {
        public FightForOwnerGoal (CloneEntity clone) {
            super(clone);
        }

        @Override
        public boolean canStart () {
            if (super.canStart() && this.owner.getAttacking() != null && this.clone.canTarget(this.owner.getAttacking()) && this.timer != this.owner.getLastAttackTime()) {
                this.target = this.owner.getAttacking();
                this.timer = this.owner.getLastAttackTime();
                return true;
            }
            return false;
        }
    }

    protected static class FollowOwnerGoal extends CloneGoal {
        private final float maxDistance;
        private final float minDistance;

        private final double speed;

        private int tickTimer = 0;

        public FollowOwnerGoal(CloneEntity clone, double speed, float maxDistance, float minDistance) {
            super(clone);
            this.speed = speed;
            this.maxDistance = maxDistance;
            this.minDistance = minDistance;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        public FollowOwnerGoal(CloneEntity clone) {
            this(clone, 1.0, 16, 6);
        }

        @Override
        public boolean canStart() {
            return super.canStart() && this.clone.distanceTo(this.owner) >= this.maxDistance;
        }

        @Override
        public void start() {
            this.clone.getNavigation().startMovingTo(this.owner, this.speed);
        }

        @Override
        public void stop() {
            this.clone.getNavigation().stop();
        }

        @Override
        public boolean shouldContinue() {
            return super.canStart() && (!this.clone.isOnGround() || this.clone.distanceTo(this.owner) >= this.minDistance);
        }

        @Override
        public void tick() {
            this.clone.getLookControl().lookAt(this.owner, 10f, this.clone.getMaxLookPitchChange());

            if (this.clone.isLeashed() || this.clone.hasVehicle() || --this.tickTimer > 0) return;
            this.tickTimer = this.getTickCount(10);
            this.clone.getNavigation().startMovingTo(this.owner, this.speed);
        }
    }
}
