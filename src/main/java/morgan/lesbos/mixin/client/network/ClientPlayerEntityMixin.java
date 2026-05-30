package morgan.lesbos.mixin.client.network;

import com.mojang.authlib.GameProfile;
import morgan.lesbos.interfaces.PossessionInterface;
import morgan.lesbos.network.packet.PossessionMoveC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow
    public Input input;

    @Shadow
    @Final
    public ClientPlayNetworkHandler networkHandler;

    @Shadow
    protected abstract void sendSprintingPacket();

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V",
                    shift = At.Shift.AFTER
            )
    )
    public void tick(CallbackInfo ci) {
        if (((PossessionInterface) this).lesbos$isPossessing()) {
            this.networkHandler.sendPacket(new PlayerInputC2SPacket(this.sidewaysSpeed, this.forwardSpeed, this.input.jumping, this.input.sneaking));
            this.sendSprintingPacket();

            MobEntity entity = ((PossessionInterface) this).lesbos$getPossessedEntity();
            if (entity != null) {
                this.setPos(entity.getX(), entity.getY(), entity.getZ());
            }
        }
    }
}
