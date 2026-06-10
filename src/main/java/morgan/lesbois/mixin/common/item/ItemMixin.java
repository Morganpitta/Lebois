package morgan.lesbois.mixin.common.item;

import morgan.lesbois.interfaces.FalteredInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin implements FalteredInterface {
    @Inject(method = "inventoryTick", at=@At("HEAD"))
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            if (this.lesbois$isFaltered(stack) && !player.getItemCooldownManager().isCoolingDown((Item) (Object) this)) {
                this.lesbois$setFaltered(stack, false);
            }
        }
    }
}
