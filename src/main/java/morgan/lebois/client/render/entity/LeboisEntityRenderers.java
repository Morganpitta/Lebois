package morgan.lebois.client.render.entity;

import morgan.lebois.entity.CoinEntity;
import morgan.lebois.entity.LeboisEntityType;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public class LeboisEntityRenderers {
    public static void register() {
        register(LeboisEntityType.GRAPPLE_HOOK, GrappleHookEntityRenderer::new);
        register(LeboisEntityType.COIN, (context) -> new FlyingItemEntityRenderer<CoinEntity>(context, 1, true));
        register(LeboisEntityType.CLONE, CloneEntityRenderer::new);
    }

    private static <T extends Entity> void register(EntityType<T> entityType, EntityRendererFactory<T> entityRendererFactory) {
        EntityRendererRegistry.register(entityType, entityRendererFactory );
    }
}