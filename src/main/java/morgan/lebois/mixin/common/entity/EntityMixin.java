package morgan.lebois.mixin.common.entity;

import io.github.apace100.apoli.component.PowerHolderComponent;
import morgan.lebois.powers.DragModifierPowerType;
import morgan.lebois.powers.ForcedFlightPowerType;
import morgan.lebois.powers.PreventSelfRenderPowerType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract boolean isOnGround();

    @Redirect(
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;isOnGround()Z"
            )
    )
    private boolean isOnGroundEnableGroundSlide(Entity entity) {
        if (!(entity instanceof LivingEntity)) return this.isOnGround();

        return this.isOnGround() || DragModifierPowerType.hasSlideMode((LivingEntity) entity);
    }

    @Inject(method = "shouldRender(D)Z", at=@At("HEAD"), cancellable = true)
    public void shouldRender(CallbackInfoReturnable<Boolean> cir) {
        if ((Entity) (Object) this instanceof LivingEntity livingEntity) {
            if (PowerHolderComponent.hasPowerType(livingEntity, PreventSelfRenderPowerType.class)) {
                cir.setReturnValue(false);
            }
        }
    }
}
