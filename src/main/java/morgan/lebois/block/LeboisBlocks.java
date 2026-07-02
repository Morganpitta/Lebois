package morgan.lebois.block;

import morgan.lebois.Lebois;
import net.minecraft.block.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class LeboisBlocks {
    public static final Block FROST_BLOCK = register("frost_block", new FrostBlock(AbstractBlock.Settings.copy(Blocks.FROSTED_ICE)));

    public static void register() {
        LeboisBlockTypes.register();
    }

    public static Block register(String path, Block block) {
        return (Block) Registry.register(Registries.BLOCK, Lebois.id(path), block);
    }
}