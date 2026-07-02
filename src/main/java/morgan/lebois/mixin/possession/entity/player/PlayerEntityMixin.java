package morgan.lebois.mixin.possession.entity.player;

import morgan.lebois.cardinalComponents.LeboisEntityComponents;
import morgan.lebois.interfaces.PossessionInterface;
import morgan.lebois.interfaces.PossessorInterface;
import morgan.lebois.network.packet.PossessionS2CPacket;
import morgan.lebois.network.packet.UnPossessionS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PossessionInterface {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public boolean lebois$isPossessing() {
        return LeboisEntityComponents.POSSESSION.get(this).isPossessing();
    }

    @Nullable
    public MobEntity lebois$getPossessedEntity() {
        return LeboisEntityComponents.POSSESSION.get(this).getPossessedEntity();
    }

    public void lebois$setPossessedEntity(@Nullable MobEntity entity) {
        LeboisEntityComponents.POSSESSION.get(this).setPossessedEntity(entity);
    }

    @Unique
    private static final Set<EntityType<?>> UNPOSSESSABLE_TYPES = Set.of(
            EntityType.ENDER_DRAGON,
            EntityType.WITHER,
            EntityType.WARDEN,
            EntityType.SHULKER
    );

    public boolean lebois$canPossess(MobEntity entity) {
        PlayerEntity possessor = ((PossessorInterface) entity).lebois$getPossessor();
        if (possessor != null && possessor != (PlayerEntity) (Object) this)
            return false;

        if (entity.isDead() || entity.isRemoved())
            return false;

        if (UNPOSSESSABLE_TYPES.contains(entity.getType()))
            return false;

        return true;
    }

    public boolean lebois$possess(MobEntity entity) {
        if (!lebois$canPossess(entity)) return false;

        if ( this.lebois$getPossessedEntity() != null && this.lebois$getPossessedEntity() != entity )
            this.lebois$unPossess();

        this.lebois$setPossessedEntity(entity);
        ((PossessorInterface) entity).lebois$setPossessor((PlayerEntity) (Object) this);

        entity.setNoGravity(true);
        ((PossessorInterface) entity).lebois$stopTargetSelectorGoals();
        Entity vehicle = entity.getVehicle();
        if (vehicle!=null) {
            entity.dismountVehicle();
            this.startRiding(vehicle);
        }

        this.setPos(entity.getPos().x, entity.getPos().y, entity.getPos().z);
        this.calculateDimensions();

        if ((PlayerEntity) (Object) this instanceof ServerPlayerEntity serverPlayerEntity) {
            ServerPlayNetworking.send(serverPlayerEntity, new PossessionS2CPacket(entity.getId()));
        }

        return true;
    }

    public void lebois$unPossess() {
        MobEntity entity = this.lebois$getPossessedEntity();

        if (entity != null) {
            entity.setNoGravity(false);
            entity.setSprinting(false);

            ((PossessorInterface) entity).lebois$setPossessor(null);
        }

        this.lebois$setPossessedEntity(null);
        this.setHealth(this.getMaxHealth());
        this.calculateDimensions();

        if ((PlayerEntity) (Object) this instanceof ServerPlayerEntity serverPlayerEntity) {
            ServerPlayNetworking.send(serverPlayerEntity, new UnPossessionS2CPacket());
        }
    }



    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        MobEntity entity = this.lebois$getPossessedEntity();

        if (entity == null || entity.isRemoved()) {
            return;
        }

        if (entity.isDead()) {
            this.lebois$unPossess();
            return;
        }

        if (((PossessorInterface) entity).lebois$getPossessor() != (PlayerEntity) (Object) this) {
            ((PossessorInterface) entity).lebois$setPossessor((PlayerEntity) (Object) this);
        }

        Box entityBoundingBox = entity.getBoundingBox();
        Box playerBoundingBox = this.getBoundingBox();

        if ( entityBoundingBox.getLengthX() != playerBoundingBox.getLengthX() ||
             entityBoundingBox.getLengthY() != playerBoundingBox.getLengthY() ||
             entityBoundingBox.getLengthZ() != playerBoundingBox.getLengthZ() ) {

            this.calculateDimensions();
        }
    }

    @Inject(method = "applyDamage", at= @At("HEAD"), cancellable = true)
    public void redirectApplyDamage(DamageSource source, float amount, CallbackInfo ci) {
        MobEntity entity = this.lebois$getPossessedEntity();

        if ( entity != null ) {
            ci.cancel();
        }
    }

    @Inject(method = "getMovementSpeed", at= @At("HEAD"), cancellable = true)
    public void getMovementSpeed(CallbackInfoReturnable<Float> cir) {
        MobEntity entity = this.lebois$getPossessedEntity();

        if ( entity != null ) {
            cir.setReturnValue((float) (this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 0.5));
        }
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;isTeammate(Lnet/minecraft/entity/Entity;)Z"
            )
    )
    private boolean redirectAttack(PlayerEntity player, Entity entity) {
        if (((PossessionInterface) player).lebois$getPossessedEntity() == entity) {
            return true;
        }

        return player.isTeammate(entity);
    }
}
