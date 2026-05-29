package morgan.lesbos.mixin.entity;

import morgan.lesbos.interfaces.DoubleJumpInterface;
import morgan.lesbos.interfaces.PossessionInterface;
import morgan.lesbos.interfaces.PossessorInterface;
import morgan.lesbos.powers.DragModifierPowerType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
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

    @Inject(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
                    args = "ldc=jump",
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

            player.setPos(player.getX(), player.getY(), player.getZ());
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
