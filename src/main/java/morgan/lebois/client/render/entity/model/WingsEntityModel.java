package morgan.lebois.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.*;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class WingsEntityModel extends AnimalModel<AbstractClientPlayerEntity> {
    public static final float DEFAULT_ANGLE = (float) (55 * (Math.PI) / 180.0F);
    public static final float FLAP_SPEED = 1.5F;
    public static final float FLAP_SIZE = (float) (20 * (Math.PI) / 180.0F);

    private final ExtrudedTextureModel leftWing;
    private final ExtrudedTextureModel rightWing;

    public WingsEntityModel(Identifier texture) {
        this.leftWing = new ExtrudedTextureModel(texture, 0, 0, 0.0F, -18.0F, 0.0F, 24, 36, 1.0F);
        this.rightWing = new ExtrudedTextureModel(texture, 24, 0, -24.0F, -18.0F, 0.0F, 24, 36, 1.0F);

        this.leftWing.pivotX = 0.0F;
        this.leftWing.pivotY = 0.0F;
        this.leftWing.pivotZ = -1.0F;

        this.rightWing.pivotX = -1.0F;
        this.rightWing.pivotY = 0.0F;
        this.rightWing.pivotZ = -1.0F;

        this.leftWing.yaw = -DEFAULT_ANGLE;
        this.rightWing.yaw = DEFAULT_ANGLE;
    }

    @Override
    public void setAngles(AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float flapAngle = MathHelper.cos(limbAngle * FLAP_SPEED) * FLAP_SIZE * limbDistance;

        this.leftWing.yaw = -(DEFAULT_ANGLE + flapAngle);
        this.rightWing.yaw = DEFAULT_ANGLE + flapAngle;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.leftWing.render(matrices, vertices, light, overlay, color);
        this.rightWing.render(matrices, vertices, light, overlay, color);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of();
    }
}