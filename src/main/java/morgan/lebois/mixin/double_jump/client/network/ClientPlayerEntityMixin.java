package morgan.lebois.mixin.double_jump.client.network;

import com.mojang.authlib.GameProfile;
import morgan.lebois.interfaces.DoubleJump;
import morgan.lebois.network.packet.DoubleJumpC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
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
    @Shadow
    @Final
    protected MinecraftClient client;
    @Unique
    private boolean wasJumping = false;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(
            method = "tickMovement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tickMovement()V", shift = At.Shift.BEFORE)
    )
    public void tickMovementDoubleJump(CallbackInfo ci) {
        if (this.input.jumping && !wasJumping) {
            if (((DoubleJump) this).lebois$canDoubleJump() && !(this.isInLava() || this.isTouchingWater())) {
                boolean boost = this.client.options.sprintKey.isPressed();

                ((DoubleJump) this).lebois$doubleJump(boost, this.input.movementForward, this.input.movementSideways);
                ClientPlayNetworking.send(new DoubleJumpC2SPacket(boost, this.input.movementForward, this.input.movementSideways));
            }
        }

        wasJumping = this.input.jumping;
    }
}