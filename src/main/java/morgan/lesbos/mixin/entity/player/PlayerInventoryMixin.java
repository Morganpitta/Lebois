package morgan.lesbos.mixin.entity.player;

import morgan.lesbos.Lesbos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin( PlayerInventory.class )
public abstract class PlayerInventoryMixin implements Inventory, Nameable {
    @Shadow @Mutable
    private DefaultedList<ItemStack> main;
    @Shadow
    public PlayerEntity player;


    @Inject(method = "<init>(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/EntityEquipment;)V",at = @At("TAIL"))
    private void init(CallbackInfo info) {
        if ( player instanceof ServerPlayerEntity ) {
            main = Lesbos.globalInventoryMain;
            System.out.println(main);
        }
    }
}