package morgan.lesbos.mixin.common.entity.player;

import morgan.lesbos.powers.DisableHungerPowerType;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class HungerManagerMixin {
    @Shadow
    private int foodLevel;

    @Inject(method = "update", at=@At("HEAD"), cancellable = true)
    public void disableHunger(PlayerEntity player, CallbackInfo ci) {
        if (DisableHungerPowerType.shouldDisableHunger(player)) {
            this.foodLevel = 20;
            ci.cancel();
        }
    }
}
