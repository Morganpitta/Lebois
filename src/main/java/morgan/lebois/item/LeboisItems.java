package morgan.lebois.item;

import morgan.lebois.Lebois;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class LeboisItems {
    public static final Item COIN = register("coin", new Item(new Item.Settings()));

    public static void register() {
    }

    public static Item register(String path, Item item) {
        return Registry.register(Registries.ITEM, RegistryKey.of(Registries.ITEM.getKey(), Lebois.id(path)), item);
    }
}
