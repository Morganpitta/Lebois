package morgan.lesbos.entity.player;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.EnumMap;

public interface EntityEquipmentInterface {
    public EnumMap<EquipmentSlot, ItemStack> getMap();
    public void setMap(EnumMap<EquipmentSlot, ItemStack> map);
}
