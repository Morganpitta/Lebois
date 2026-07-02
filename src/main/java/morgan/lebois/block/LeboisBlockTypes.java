package morgan.lebois.block;

import morgan.lebois.Lebois;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class LeboisBlockTypes {
    public static void register() {
        Registry.register(Registries.BLOCK_TYPE, Lebois.id("frost_block"), FrostBlock.CODEC);
    }
}
