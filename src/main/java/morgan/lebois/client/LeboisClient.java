package morgan.lebois.client;

import morgan.lebois.Lebois;
import morgan.lebois.client.render.LeboisRenderLayer;
import morgan.lebois.client.render.entity.LeboisEntityRenderers;
import morgan.lebois.client.render.entity.WingsModelCacheManager;
import morgan.lebois.network.packet.LeboisClientPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LeboisClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LeboisEntityRenderers.register();
        LeboisClientPackets.register();
        LeboisRenderLayer.register();
        WingsModelCacheManager.register();

        Lebois.LOGGER.info("Lebois Client initialised!!!!!");
    }
}