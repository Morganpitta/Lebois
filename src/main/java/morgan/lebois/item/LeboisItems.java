package morgan.lebois.item;

import morgan.lebois.Lebois;
import net.minecraft.block.jukebox.JukeboxSongs;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.component.type.JukeboxPlayableComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Rarity;

public class LeboisItems {
    public static final Item PATRICK = register("patrick", new Item(
        new Item.Settings().rarity(Rarity.EPIC).food(FoodComponents.ENCHANTED_GOLDEN_APPLE).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true).fireproof().jukeboxPlayable(JukeboxSongs.PIGSTEP)
    ));

    public static void register() {
    }

    public static Item register(String path, Item item) {
        return Registry.register(Registries.ITEM, RegistryKey.of(Registries.ITEM.getKey(), Lebois.id(path)), item);
    }
}
