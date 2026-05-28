package morgan.lesbos.mixin.client.network;

import com.mojang.authlib.GameProfile;
import morgan.lesbos.interfaces.PossessionInterface;
import morgan.lesbos.network.packet.PossessionInputC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {
    @Shadow
    public Input input;

    public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "tick", at=@At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (((PossessionInterface) this).lesbos$isPossessing()) {
            ClientPlayNetworking.send(new PossessionInputC2SPacket(
                    this.sidewaysSpeed,
                    this.forwardSpeed,
                    this.input.jumping,
                    this.input.sneaking
            ));
        }
    }
}
