package morgan.lebois.client.render.entity;

import morgan.lebois.Lebois;
import morgan.lebois.client.render.entity.model.WingsEntityModel;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WingsModelCacheManager {
    private record Key(Identifier texture, int width, int height) {}

    private static final Map<Key, WingsEntityModel> cachedModelMap = new ConcurrentHashMap<>();

    public static void register() {
        registerReloader();
    }

    public static WingsEntityModel getOrCreate(Identifier texture, int width, int height) {
        return cachedModelMap.computeIfAbsent(
                new Key(texture, width, height),
                (key) -> new WingsEntityModel(key.texture(), key.width(), key.height())
        );
    }

    public static void clearCache() {
        cachedModelMap.clear();
    }

    public static void registerReloader() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return Lebois.id("wings_cache");
                    }

                    @Override
                    public void reload(ResourceManager manager) {
                        clearCache();
                    }
                }
        );
    }
}