package morgan.lesbos.mixin.entity;

import morgan.lesbos.Lesbos;
import morgan.lesbos.interfaces.DoubleJumpInterface;
import morgan.lesbos.interfaces.PossessionInterface;
import morgan.lesbos.interfaces.PossessorInterface;
import morgan.lesbos.network.packet.PossessionMoveC2SPacket;
import morgan.lesbos.powers.DragModifierPowerType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public float forwardSpeed;

    @Shadow
    public float sidewaysSpeed;

    @Shadow
    public abstract void setJumping(boolean jumping);

    @Shadow
    public abstract void setMovementSpeed(float movementSpeed);

    @Shadow
    public abstract double getAttributeValue(RegistryEntry<EntityAttribute> attribute);

    @Shadow
    public abstract float getMovementSpeed();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyConstant(
            method = "travel",
            constant = @Constant(floatValue = 0.91F)
    )
    public float travelModifyAirDrag(float constant) {
        return DragModifierPowerType.getAirDrag((LivingEntity) (Object) this);
    }


    // Trying to inject before the jump logic
    @Inject(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
                    ordinal = 2,
                    shift = At.Shift.BEFORE
            )
    )
    public void tickMovement(CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof MobEntity) {
            PlayerEntity player = ((PossessorInterface) this).lesbos$getPossessor();

            if (player == null) return;

            this.setYaw(player.getYaw());
            this.setPitch(player.getPitch());
            this.setHeadYaw(player.getHeadYaw());

            this.sidewaysSpeed = player.sidewaysSpeed;
            this.forwardSpeed = player.forwardSpeed;

            this.setJumping(((LivingEntityAccessor) player).lesbos$isJumping());
            this.setSneaking(player.isSneaking());

            if (this.getMovementSpeed() == 0) {
                this.setMovementSpeed((float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
            }
        }
    }

//    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
//    public void travel(Vec3d movementInput, CallbackInfo ci) {
//        if ((LivingEntity) (Object) this instanceof MobEntity && !this.getWorld().isClient()) {
//            PlayerEntity player = ((PossessorInterface) this).lesbos$getPossessor();
//
//            if (player == null) return;
//
//            ci.cancel();
//        }
//    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof MobEntity) {
            PlayerEntity player = ((PossessorInterface) this).lesbos$getPossessor();

            if (player == null) return;

            player.setPos(this.getX(), this.getY(), this.getZ());

            if ( this.getWorld().isClient() ) {
                MinecraftClient client = MinecraftClient.getInstance();

                if (client.player == null) return;

                if (((PossessorInterface) this).lesbos$getPossessor() != client.player) return;

                ClientPlayNetworking.send(new PossessionMoveC2SPacket((MobEntity) (Object) this));
            }
        }
    }

    @Inject(method = "getHealth", at = @At("HEAD"), cancellable = true)
    private void redirectHealth(CallbackInfoReturnable<Float> cir) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player) {
            PossessionInterface possession = (PossessionInterface) player;

            if (possession.lesbos$isPossessing()) {
                MobEntity entity = possession.lesbos$getPossessedEntity();

                if (entity != null) {
                    cir.setReturnValue(entity.getHealth());
                }
            }
        }
    }

    @Inject(method = "getAttributeValue", at = @At("HEAD"), cancellable = true)
    private void redirectAttributes(RegistryEntry<EntityAttribute> attribute, CallbackInfoReturnable<Double> cir) {
        if ((LivingEntity) (Object) this instanceof PlayerEntity player) {
            PossessionInterface possession = (PossessionInterface) player;

            if (possession.lesbos$isPossessing()) {
                MobEntity entity = possession.lesbos$getPossessedEntity();

                if (entity != null && entity.getAttributes().hasAttribute(attribute)) {
                    cir.setReturnValue(entity.getAttributeValue(attribute));
                }
            }
        }
    }
}
