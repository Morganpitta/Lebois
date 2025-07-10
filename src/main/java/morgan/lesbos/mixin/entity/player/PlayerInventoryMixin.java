package morgan.lesbos.mixin.entity.player;

import morgan.lesbos.Lesbos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin( PlayerInventory.class )
public abstract class PlayerInventoryMixin implements Inventory, Nameable {
    @Shadow @Mutable
    private DefaultedList<ItemStack> main;
    @Shadow @Mutable
    private EntityEquipment equipment;

    @Redirect(method = "<init>(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/EntityEquipment;)V",at = @At(value = "FIELD",target = "net/minecraft/entity/player/PlayerInventory.main : Lnet/minecraft/util/collection/DefaultedList;", opcode = Opcodes.PUTFIELD))
    private void inventoryRedirect( PlayerInventory playerInventory, DefaultedList<ItemStack> rhs ) {
        main = Lesbos.globalInventory;
        System.out.println(main);
    }

    @Redirect(method = "<init>(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/EntityEquipment;)V",at = @At(value = "FIELD",target = "net/minecraft/entity/player/PlayerInventory.equipment : Lnet/minecraft/entity/EntityEquipment;", opcode = Opcodes.PUTFIELD))
    private void equipmentRedirect( PlayerInventory playerInventory, EntityEquipment rhs ) {
        equipment = Lesbos.equipment;
        System.out.println(equipment);
    }
}