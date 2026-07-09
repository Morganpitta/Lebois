package morgan.lebois.client.render.entity.feature;

import morgan.lebois.client.render.entity.WingsModelCacheManager;
import morgan.lebois.client.render.entity.model.WingsEntityModel;
import morgan.lebois.interfaces.Winged;
import morgan.lebois.powers.WingsPowerType;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class WingsFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public WingsFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!WingsPowerType.hasWings(entity)) return;

        Identifier texture = WingsPowerType.getTexture(entity);
        int width = WingsPowerType.getWidth(entity);
        int height = WingsPowerType.getHeight(entity);
        float scale = WingsPowerType.getScale(entity);
        float offset = WingsPowerType.getOffset(entity);
        if (texture == null || width <= 0 || height <= 0) return;

        WingsEntityModel model = WingsModelCacheManager.getOrCreate(texture, width, height);
        if (model == null) return;

        matrices.push();

        float yOffset = (6.0F + offset) / 16.0F * this.getContextModel().body.yScale;
        float zOffset = (2.0F - 0.25F) / 16.0F * this.getContextModel().body.zScale;

        this.getContextModel().body.rotate(matrices);
        matrices.translate(0.0F, yOffset, zOffset);
        matrices.scale(scale, scale, scale);

        this.getContextModel().copyStateTo(model);

        float wingAngle = MathHelper.lerp(tickDelta, ((Winged) entity).lebois$getPrevWingAngle(), ((Winged) entity).lebois$getWingAngle());
        float wingDistance = MathHelper.lerp(tickDelta, ((Winged) entity).lebois$getPrevWingDistance(), ((Winged) entity).lebois$getWingDistance());

        model.setAngles(entity, wingAngle, wingDistance, animationProgress, headYaw, headPitch);

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(texture));
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
    }
}