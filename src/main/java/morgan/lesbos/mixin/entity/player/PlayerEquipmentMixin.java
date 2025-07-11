package morgan.lesbos.mixin.entity.player;

import com.llamalad7.mixinextras.sugar.Local;
import morgan.lesbos.Lesbos;
import morgan.lesbos.entity.player.EntityEquipmentInterface;
import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEquipment;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEquipment.class)
public abstract class PlayerEquipmentMixin extends EntityEquipment {
    @Inject(method = "<init>(Lnet/minecraft/entity/player/PlayerEntity;)V",at = @At("TAIL"))
    private void init( CallbackInfo info, @Local PlayerEntity player ) {
        if (player instanceof ServerPlayerEntity) {
            ((EntityEquipmentInterface) (EntityEquipment) (Object) this).setMap(Lesbos.globalInventoryMap);
        }
    }
}
