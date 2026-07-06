package morgan.lebois.client.render.entity.model;

import morgan.lebois.Lebois;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExtrudedTextureModel {

    public record ExtrudedQuad(
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4,
            float u1, float v1, float u2, float v2,
            Vector3f direction
    ) {}

    private final List<ExtrudedQuad> quads;

    public float pivotX;
    public float pivotY;
    public float pivotZ;
    public float pitch;
    public float yaw;
    public float roll;
    public float xScale = 1.0F;
    public float yScale = 1.0F;
    public float zScale = 1.0F;
    public boolean visible = true;

    public ExtrudedTextureModel(Identifier textureId, int offsetU, int offsetV, float offsetX, float offsetY, int width, int height, float depth) {
        this.quads = generateExtrudedQuads(textureId, offsetU, offsetV, offsetX, offsetY, width, height, depth);
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        if (!this.visible || this.quads.isEmpty()) return;

        matrices.push();

        // --- COPIED DIRECTLY FROM ModelPart#rotate ---
        matrices.translate(this.pivotX / 16.0F, this.pivotY / 16.0F, this.pivotZ / 16.0F);
        if (this.pitch != 0.0F || this.yaw != 0.0F || this.roll != 0.0F) {
            matrices.multiply((new Quaternionf()).rotationZYX(this.roll, this.yaw, this.pitch));
        }
        if (this.xScale != 1.0F || this.yScale != 1.0F || this.zScale != 1.0F) {
            matrices.scale(this.xScale, this.yScale, this.zScale);
        }

        MatrixStack.Entry entry = matrices.peek();
        Matrix4f positionMatrix = entry.getPositionMatrix();
        Vector3f normalBuffer = new Vector3f();

        for (ExtrudedQuad quad : this.quads) {
            // --- COPIED DIRECTLY FROM ModelPart#renderCuboids Normal Transformation ---
            Vector3f transformedNormal = entry.transformNormal(quad.direction, normalBuffer);
            float nx = transformedNormal.x();
            float ny = transformedNormal.y();
            float nz = transformedNormal.z();

            // Pass the raw overlay color directly through to prevent color degradation
            renderVertex(positionMatrix, vertices, quad.x1, quad.y1, quad.z1, quad.u1, quad.v1, overlay, light, nx, ny, nz, color);
            renderVertex(positionMatrix, vertices, quad.x2, quad.y2, quad.z2, quad.u1, quad.v2, overlay, light, nx, ny, nz, color);
            renderVertex(positionMatrix, vertices, quad.x3, quad.y3, quad.z3, quad.u2, quad.v2, overlay, light, nx, ny, nz, color);
            renderVertex(positionMatrix, vertices, quad.x4, quad.y4, quad.z4, quad.u2, quad.v1, overlay, light, nx, ny, nz, color);
        }

        matrices.pop();
    }

    private void renderVertex(Matrix4f matrix, VertexConsumer consumer, float x, float y, float z, float u, float v, int overlay, int light, float nx, float ny, float nz, int color) {
        // --- COPIED DIRECTLY FROM ModelPart.Cuboid#renderCuboid Vertex Scaling ---
        float blockX = x / 16.0F;
        float blockY = y / 16.0F;
        float blockZ = z / 16.0F;
        Vector3f posBuffer = matrix.transformPosition(blockX, blockY, blockZ, new Vector3f());

        consumer.vertex(posBuffer.x(), posBuffer.y(), posBuffer.z(), color, u, v, overlay, light, nx, ny, nz);
    }

    private static List<ExtrudedQuad> generateExtrudedQuads(Identifier textureId, int offsetU, int offsetV, float offsetX, float offsetY, int width, int height, float depth) {
        List<ExtrudedQuad> generatedQuads = new ArrayList<>();
        try (NativeImage img = loadNativeImageFromResource(textureId)) {
            if (img == null) return generatedQuads;
            float texW = (float) img.getWidth();
            float texH = (float) img.getHeight();

            float fullU1 = offsetU / texW;
            float fullV1 = offsetV / texH;
            float fullU2 = (offsetU + width) / texW;
            float fullV2 = (offsetV + height) / texH;

            float endX = offsetX + width;
            float endY = offsetY + height;

            // --- FIXED PANEL WINDING & COORDINATES ---
            // Front Face (z = depth)
            generatedQuads.add(new ExtrudedQuad(
                    endX, offsetY, depth,
                    endX, endY, depth,
                    offsetX, endY, depth,
                    offsetX, offsetY, depth,
                    fullU1, fullV1, fullU2, fullV2, new Vector3f(0, 0, 1)
            ));

            // Back Face (z = 0.0F)
            generatedQuads.add(new ExtrudedQuad(
                    offsetX, offsetY, 0.0F,
                    offsetX, endY, 0.0F,
                    endX, endY, 0.0F,
                    endX, offsetY, 0.0F,
                    fullU2, fullV1, fullU1, fullV2, new Vector3f(0, 0, -1)
            ));

            // --- FIXED SIDE STRIPS & DIRECTION CORRECTIONS ---
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    // Read texture right-to-left to cancel out the global texture flip
                    int u = offsetU + (width - 1 - x);
                    int v = offsetV + y;

                    if (isTransparent(img, u, v)) continue;

                    // Track positions along flipped structural orientation
                    float px1 = offsetX + x;
                    float px2 = px1 + 1.0F;
                    float py1 = offsetY + y;
                    float py2 = py1 + 1.0F;

                    float u1 = u / texW;
                    float v1 = v / texH;
                    float u2 = (u + 1) / texW;
                    float v2 = (v + 1) / texH;

                    // Top Edge (Y-)
                    if (y == 0 || isTransparent(img, u, v - 1)) {
                        generatedQuads.add(new ExtrudedQuad(px2, py1, 0.0F, px2, py1, depth, px1, py1, depth, px1, py1, 0.0F, u1, v1, u2, v2, new Vector3f(0, -1, 0)));
                    }
                    // Bottom Edge (Y+)
                    if (y == height - 1 || isTransparent(img, u, v + 1)) {
                        generatedQuads.add(new ExtrudedQuad(px1, py2, 0.0F, px1, py2, depth, px2, py2, depth, px2, py2, 0.0F, u1, v1, u2, v2, new Vector3f(0, 1, 0)));
                    }
                    // Left Edge (X-)
                    if (x == 0 || isTransparent(img, offsetU + (width - x), v)) {
                        generatedQuads.add(new ExtrudedQuad(px1, py1, 0.0F, px1, py1, depth, px1, py2, depth, px1, py2, 0.0F, u1, v1, u2, v2, new Vector3f(-1, 0, 0)));
                    }
                    // Right Edge (X+)
                    if (x == width - 1 || isTransparent(img, offsetU + (width - 2 - x), v)) {
                        generatedQuads.add(new ExtrudedQuad(px2, py2, 0.0F, px2, py2, depth, px2, py1, depth, px2, py1, 0.0F, u1, v1, u2, v2, new Vector3f(1, 0, 0)));
                    }
                }
            }
        } catch (Exception e) {
            Lebois.LOGGER.warn("Failed to generate custom model extrusion data for texture: {}", textureId);
        }
        return generatedQuads;
    }

    private static NativeImage loadNativeImageFromResource(Identifier id) {
        MinecraftClient client = MinecraftClient.getInstance();
        Optional<Resource> resource = client.getResourceManager().getResource(id);
        if (resource.isPresent()) {
            try (InputStream stream = resource.get().getInputStream()) {
                return NativeImage.read(stream);
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    private static boolean isTransparent(NativeImage img, int u, int v) {
        if (u < 0 || v < 0 || u >= img.getWidth() || v >= img.getHeight()) return true;
        return (img.getColor(u, v) >>> 24) < 10;
    }
}