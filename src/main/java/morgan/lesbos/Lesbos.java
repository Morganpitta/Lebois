package morgan.lesbos;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.EntityEquipment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lesbos implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final String MOD_ID = "Lesbos";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static DefaultedList<ItemStack> globalInventory = DefaultedList.ofSize(36, ItemStack.EMPTY);
    public static EntityEquipment equipment = new EntityEquipment();

    @Override
    public void onInitialize() {
        LOGGER.info("Mod initialised");
    }
}
