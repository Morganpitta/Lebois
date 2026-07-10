package morgan.lebois.client.render.entity;

import morgan.lebois.Lebois;
import morgan.lebois.entity.CloneEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

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
    public void render(CloneEntity clone, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.setModelPose(clone);
        super.render(clone, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Vec3d getPositionOffset(CloneEntity clone, float f) {
        return clone.isSitting()
                ? new Vec3d(0.0, clone.getScale() * -2.0F / 16.0, 0.0)
                : super.getPositionOffset(clone, f);
    }

    private void setModelPose(CloneEntity clone) {
        this.model.sneaking = clone.isSitting();
    }

        @Override
    public Identifier getTexture(CloneEntity entity) {
        return Lebois.id("textures/entity/skin.png");
    }
}