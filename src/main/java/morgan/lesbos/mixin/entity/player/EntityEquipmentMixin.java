package morgan.lesbos.mixin.entity.player;

import morgan.lesbos.entity.player.EntityEquipmentInterface;
import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.EnumMap;

@Mixin(EntityEquipment.class)
public abstract class EntityEquipmentMixin implements EntityEquipmentInterface {
    @Shadow @Mutable
    private EnumMap<EquipmentSlot, ItemStack> map;

    public EnumMap<EquipmentSlot, ItemStack> getMap() {
        return map;
    }

    public void setMap( EnumMap<EquipmentSlot, ItemStack> newMap ) {
        map = newMap;
    }
}
