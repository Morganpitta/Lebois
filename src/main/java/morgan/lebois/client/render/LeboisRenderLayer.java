package morgan.lebois.client.render;

import morgan.lebois.block.LeboisBlocks;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class LeboisRenderLayer {
    public static void register() {
        BlockRenderLayerMap.INSTANCE.putBlock(LeboisBlocks.FROST_BLOCK, RenderLayer.getTranslucent());
    }
}
