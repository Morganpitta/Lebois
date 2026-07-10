package morgan.lebois.client.render.entity;

import morgan.lebois.entity.CloneEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)

public class CloneEntityRenderer extends BipedEntityRenderer<CloneEntity, PlayerEntityModel<CloneEntity>> {
    public CloneEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PlayerEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_SLIM), true), 0.5F);

        this.addFeature(
                new ArmorFeatureRenderer<>(
                        this,
                        new ArmorEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_SLIM_INNER_ARMOR)),
                        new ArmorEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR)),
                        ctx.getModelManager()
                )
        );
    }

    @Override
    public Identifier getTexture(CloneEntity entity) {
        return DefaultSkinHelper.getTexture();
    }
}