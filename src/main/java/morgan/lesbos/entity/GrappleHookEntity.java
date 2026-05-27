package morgan.lesbos.entity;

import morgan.lesbos.interfaces.GrappleInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GrappleHookEntity extends Entity implements Ownable {
    @Nullable
    private PlayerEntity owner;
    public double minDistance;
    public double lookAssist;
    public double pullSpeed;
    public double damping;

    // Ideally a 3:2 ratio
    public static final double pullFactorModifier = 0.2;
    public static final double lookAssistModifier = 0.13;

    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(
            GrappleHookEntity.class, TrackedDataHandlerRegistry.INTEGER
    );

    public GrappleHookEntity(EntityType<GrappleHookEntity> type, World world) {
        super(type, world);
    }

    public GrappleHookEntity(World world, @Nullable PlayerEntity owner, Vec3d position, double minDistance, double pullSpeed, double lookAssist, double damping) {
        super(LesbosEntities.GRAPPLE_HOOK, world);
        this.setOwner(owner);
        this.setPosition(position);
        this.minDistance = minDistance;
        this.lookAssist = lookAssist;
        this.pullSpeed = pullSpeed;
        this.damping = damping;
    }

    @Override
    @Nullable
    public PlayerEntity getOwner() {
        return this.owner;
    }

    public void setOwner(@Nullable PlayerEntity owner) {
        this.owner = owner;
        this.dataTracker.set(OWNER_ID, owner != null ? owner.getId() : -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            PlayerEntity owner = this.getOwner();
            if (owner == null || owner.isDead()) {
                if (owner != null) {
                    ((GrappleInterface) (Object) owner).lesbos$setGrappleHook(null);
                }

                this.discard();
                return;
            }

            if (owner instanceof ServerPlayerEntity serverPlayer) {
                Vec3d direction = this.getPos().subtract(owner.getPos()).normalize();
                Vec3d playerDirection = serverPlayer.getRotationVector().normalize();
                Vec3d velocity = owner.getVelocity();

                double distanceSq = this.getPos().squaredDistanceTo(owner.getPos());
                double pullSpeed = this.pullSpeed;

                if ( distanceSq  < (minDistance * minDistance) ) pullSpeed *= (distanceSq/(minDistance*minDistance));

                serverPlayer.setVelocity(
                        velocity.x * (1-Math.clamp(this.damping, 0, 1)) + direction.x * pullFactorModifier * pullSpeed + playerDirection.x * lookAssistModifier * this.lookAssist,
                        velocity.y * (1-Math.clamp(this.damping, 0, 1)) + direction.y * pullFactorModifier * pullSpeed + playerDirection.y * lookAssistModifier * this.lookAssist,
                        velocity.z * (1-Math.clamp(this.damping, 0, 1)) + direction.z * pullFactorModifier * pullSpeed + playerDirection.z * lookAssistModifier * this.lookAssist
                );

                serverPlayer.velocityDirty = true;
                serverPlayer.velocityModified = true;

                serverPlayer.networkHandler.sendPacket(
                        new EntityVelocityUpdateS2CPacket(serverPlayer)
                );
            }
        }
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(OWNER_ID, -1);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (OWNER_ID.equals(data)) {
            int id = this.dataTracker.get(OWNER_ID);
            this.owner = id != -1 ? (PlayerEntity) this.getWorld().getEntityById(id) : null;
            if ( this.owner != null && ((GrappleInterface) this.owner).lesbos$getGrappleHook() == null) ((GrappleInterface) this.owner).lesbos$setGrappleHook(this);
        }
        super.onTrackedDataSet(data);
    }

    @Override
    public void onRemoved() {
        if (owner instanceof GrappleInterface grappleOwner && grappleOwner.lesbos$getGrappleHook() == this) {
            grappleOwner.lesbos$setGrappleHook(null);
        }
        super.onRemoved();
    }

    // Entity is non-persistent, no need to save or have any additional data
    @Override
    public boolean shouldSave() {return false;}
    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}
    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}
}
