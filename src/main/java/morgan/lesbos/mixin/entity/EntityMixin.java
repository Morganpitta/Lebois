package morgan.lesbos.mixin.entity;

import morgan.lesbos.interfaces.DoubleJumpInterface;
import morgan.lesbos.powers.DragModifierPowerType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract boolean isOnGround();

    @Shadow
    public abstract boolean isTouchingWater();

    @Shadow
    public abstract boolean isInLava();

    @Inject(method = "onLanding", at = @At("HEAD"))
    public void onLandingResetDoubleJumps(CallbackInfo ci) {
        if (!((Entity) (Object) this instanceof PlayerEntity)) return;
        DoubleJumpInterface doubleJumps = (DoubleJumpInterface) this;

        if (this.isOnGround() && doubleJumps.lesbos$getDoubleJumps() < doubleJumps.lesbos$getMaxDoubleJumps()) {
            doubleJumps.lesbos$setDoubleJumps(doubleJumps.lesbos$getMaxDoubleJumps());
        } else if ((this.isInLava() || this.isTouchingWater()) && doubleJumps.lesbos$getDoubleJumps() <= doubleJumps.lesbos$getMaxDoubleJumps()) {
            doubleJumps.lesbos$setDoubleJumps(doubleJumps.lesbos$getMaxDoubleJumps() + 1);
        }
    }

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
}
