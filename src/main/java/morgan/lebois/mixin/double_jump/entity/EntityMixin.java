package morgan.lebois.mixin.double_jump.entity;

import morgan.lebois.interfaces.DoubleJump;
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
    public void onLandingResetDoubleJumps(CallbackInfo ci) {
        if (!((Entity) (Object) this instanceof PlayerEntity)) return;
        DoubleJump doubleJumps = (DoubleJump) this;

        if (this.isOnGround() && doubleJumps.lebois$getDoubleJumps() < doubleJumps.lebois$getMaxDoubleJumps()) {
            doubleJumps.lebois$setDoubleJumps(doubleJumps.lebois$getMaxDoubleJumps());
        } else if ((this.isInLava() || this.isTouchingWater()) && doubleJumps.lebois$getDoubleJumps() <= doubleJumps.lebois$getMaxDoubleJumps()) {
            doubleJumps.lebois$setDoubleJumps(doubleJumps.lebois$getMaxDoubleJumps() + 1);
        }
    }
}
