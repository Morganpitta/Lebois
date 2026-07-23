package morgan.lebois.mixin.common.client.player;

import io.github.apace100.apoli.component.PowerHolderComponent;
import morgan.lebois.powers.ForcedFlightPowerType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "isFlyingLocked", at=@At("HEAD"), cancellable = true)
    public void forceFlight(CallbackInfoReturnable<Boolean> cir) {
        if (PowerHolderComponent.hasPowerType(this.client.player, ForcedFlightPowerType.class)) {
            cir.setReturnValue(true);
        }
    }
}
