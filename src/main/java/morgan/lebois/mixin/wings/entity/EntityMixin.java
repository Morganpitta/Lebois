package morgan.lebois.mixin.wings.entity;

import morgan.lebois.interfaces.Winged;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract boolean isOnGround();

    @Shadow
    public abstract boolean isTouchingWater();

    @Shadow
    public abstract boolean isInLava();

    @Shadow
    public abstract World getWorld();

    @Inject(method = "onLanding", at = @At("HEAD"))
    public void onLandingResetFlyingTime(CallbackInfo ci) {
        if (!((Entity) (Object) this instanceof PlayerEntity)) return;
        Winged winged = (Winged) this;

        if ((this.isOnGround() || this.isInLava() || this.isTouchingWater()) && winged.lebois$getFlyingTime() > 0) {
            winged.lebois$setFlyingTime(0);
        }
    }
}