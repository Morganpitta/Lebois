package morgan.lebois.mixin.common.client.network;

import io.github.apace100.apoli.component.PowerHolderComponent;
import morgan.lebois.powers.PreventSelfRenderPowerType;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OtherClientPlayerEntity.class)
public class OtherClientPlayerEntityMixin {
    @Inject(method = "shouldRender(D)Z", at=@At("HEAD"), cancellable = true)
    public void shouldRender(CallbackInfoReturnable<Boolean> cir) {
        if ((Entity) (Object) this instanceof LivingEntity livingEntity) {
            if (PowerHolderComponent.hasPowerType(livingEntity, PreventSelfRenderPowerType.class)) {
                cir.setReturnValue(false);
            }
        }
    }
}
