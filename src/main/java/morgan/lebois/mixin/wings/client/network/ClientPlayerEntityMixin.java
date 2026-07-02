package morgan.lebois.mixin.wings.client.network;

import com.mojang.authlib.GameProfile;
import morgan.lebois.interfaces.Winged;
import morgan.lebois.network.packet.FlyingC2SPacket;
import morgan.lebois.powers.WingsPowerType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow
    public Input input;
    @Unique
    private boolean wasJumping = false;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(
            method = "tickMovement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tickMovement()V", shift = At.Shift.BEFORE)
    )
    public void tickMovementWings(CallbackInfo ci) {
        if (this.input.jumping) {
            if (WingsPowerType.hasWings(this) && !this.isOnGround() && !this.isTouchingWater() && !((Winged) this).lebois$isFlying() && !wasJumping) {
                ((Winged) this).lebois$setFlying(true);
                ClientPlayNetworking.send(new FlyingC2SPacket(true));
            }
        }
        else if (((Winged) this).lebois$isFlying()) {
            ((Winged) this).lebois$setFlying(false);
            ClientPlayNetworking.send(new FlyingC2SPacket(false));
        }

        wasJumping = this.input.jumping;
    }

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    public void tickWings(CallbackInfo ci) {
        ((Winged) this).lebois$updateWings();
    }
}