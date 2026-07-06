package morgan.lebois.client.render.entity.model;

import morgan.lebois.Lebois;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Basically just mimicking how ModelPart works, but for extruded textures
@Environment(EnvType.CLIENT)
public class ExtrudedTextureModel {
    private final List<Quad> quads;

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
    public boolean hidden = false;

    public ExtrudedTextureModel(Identifier textureId, int offsetU, int offsetV, float offsetX, float offsetY, float offsetZ, int width, int height, float depth) {
        this.quads = generateQuads(textureId, offsetU, offsetV, offsetX, offsetY, offsetZ, width, height, depth);
    }

    // See ModelPart#render()
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        if (this.visible) {
            if (!this.quads.isEmpty()) {
                matrices.push();

                this.rotate(matrices);

                if (!this.hidden) {
                    this.renderQuads(matrices.peek(), vertices, light, overlay, color);
                }

                matrices.pop();
            }
        }
    }

    public void rotate(MatrixStack matrices) {
        matrices.translate(this.pivotX / 16.0F, this.pivotY / 16.0F, this.pivotZ / 16.0F);
        if (this.pitch != 0.0F || this.yaw != 0.0F || this.roll != 0.0F) {
            matrices.multiply((new Quaternionf()).rotationZYX(this.roll, this.yaw, this.pitch));
        }

        if (this.xScale != 1.0F || this.yScale != 1.0F || this.zScale != 1.0F) {
            matrices.scale(this.xScale, this.yScale, this.zScale);
        }
    }

    private static List<Quad> generateQuads(Identifier textureId, int offsetU, int offsetV, float offsetX, float offsetY, float offsetZ, int width, int height, float depth) {
        List<Quad> generatedQuads = new ArrayList<>();
        try (NativeImage img = loadNativeImageFromResource(textureId)) {
            if (img == null) return generatedQuads;

            float textureWidth = (float) img.getWidth();
            float textureHeight = (float) img.getHeight();

            float startU = offsetU / textureWidth;
            float startV = offsetV / textureHeight;
            float endU = (offsetU + width) / textureWidth;
            float endV = (offsetV + height) / textureHeight;

            float startX = offsetX;
            float startY = offsetY;
            float startZ = offsetZ;

            float endX = offsetX + width;
            float endY = offsetY + height;
            float endZ = offsetZ + depth;

            // Front face (Direction.SOUTH)
            Vertex[] frontVertices = new Vertex[]{
                    new Vertex(startX, startY, endZ, 0, 0),
                    new Vertex(endX, startY, endZ, 0, 0),
                    new Vertex(endX, endY, endZ, 0, 0),
                    new Vertex(startX, endY, endZ, 0, 0)
            };
            generatedQuads.add(new Quad(frontVertices, startU, startV, endU, endV, 1.0F, 1.0F, false, Direction.NORTH));

            // Back face (Direction.NORTH)
            Vertex[] backVertices = new Vertex[]{
                    new Vertex(endX, startY, startZ, 0, 0),
                    new Vertex(startX, startY, startZ, 0, 0),
                    new Vertex(startX, endY, startZ, 0, 0),
                    new Vertex(endX, endY, startZ, 0, 0)
            };
            generatedQuads.add(new Quad(backVertices, endU, startV, startU, endV, 1.0F, 1.0F, false, Direction.SOUTH));

            // Side Quads
            for (int xIndex = 0; xIndex < width; xIndex++) {
                for (int yIndex = 0; yIndex < height; yIndex++) {
                    int u = offsetU + (width - 1 - xIndex);
                    int v = offsetV + yIndex;

                    if (isTransparent(img, u, v)) continue;

                    startX = offsetX + xIndex;
                    endX = startX + 1.0F;
                    startY = offsetY + yIndex;
                    endY = startY + 1.0F;

                    startU = u / textureWidth;
                    startV = v / textureHeight;
                    endU = (u + 1) / textureWidth;
                    endV = (v + 1) / textureHeight;

                    // Top face (Direction.UP)
                    if (yIndex == 0 || isTransparent(img, u, v - 1)) {
                        Vertex[] topVertices = new Vertex[]{
                                new Vertex(startX, startY, startZ, 0, 0),
                                new Vertex(endX, startY, startZ, 0, 0),
                                new Vertex(endX, startY, endZ, 0, 0),
                                new Vertex(startX, startY, endZ, 0, 0)
                        };
                        generatedQuads.add(new Quad(topVertices, startU, startV, endU, endV, 1.0F, 1.0F, false, Direction.UP));
                    }
                    // Bottom face (Direction.DOWN)
                    if (yIndex == height - 1 || isTransparent(img, u, v + 1)) {
                        Vertex[] bottomVertices = new Vertex[]{
                                new Vertex(startX, endY, endZ, 0, 0),
                                new Vertex(endX, endY, endZ, 0, 0),
                                new Vertex(endX, endY, startZ, 0, 0),
                                new Vertex(startX, endY, startZ, 0, 0)
                        };
                        generatedQuads.add(new Quad(bottomVertices, startU, startV, endU, endV, 1.0F, 1.0F, false, Direction.DOWN));
                    }
                    // Left face (Direction.WEST)
                    if (xIndex == 0 || isTransparent(img, u + 1, v)) {
                        Vertex[] leftVertices = new Vertex[]{
                                new Vertex(startX, startY, startZ, 0, 0),
                                new Vertex(startX, startY, endZ, 0, 0),
                                new Vertex(startX, endY, endZ, 0, 0),
                                new Vertex(startX, endY, startZ, 0, 0)
                        };
                        generatedQuads.add(new Quad(leftVertices, startU, startV, endU, endV, 1.0F, 1.0F, false, Direction.WEST));
                    }
                    // Right face (Direction.EAST)
                    if (xIndex == width - 1 || isTransparent(img, u - 1, v)) {
                        Vertex[] rightVertices = new Vertex[]{
                                new Vertex(endX, startY, endZ, 0, 0),
                                new Vertex(endX, startY, startZ, 0, 0),
                                new Vertex(endX, endY, startZ, 0, 0),
                                new Vertex(endX, endY, endZ, 0, 0)
                        };
                        generatedQuads.add(new Quad(rightVertices, startU, startV, endU, endV, 1.0F, 1.0F, false, Direction.EAST));
                    }
                }
            }
        } catch (Exception e) {
            Lebois.LOGGER.warn("Failed to generate ExtrudedTextureModel from ID: {}", textureId);
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


    // See ModelPart#Quad
    @Environment(EnvType.CLIENT)
    public static class Quad {
        public final Vertex[] vertices;
        public final Vector3f direction;

        public Quad(Vertex[] vertices, float u1, float v1, float u2, float v2, float squishU, float squishV, boolean flip, Direction direction) {
            this.vertices = vertices;
            float f = 0.0F / squishU;
            float g = 0.0F / squishV;
            vertices[0] = vertices[0].remap(u2 / squishU - f, v1 / squishV + g);
            vertices[1] = vertices[1].remap(u1 / squishU + f, v1 / squishV + g);
            vertices[2] = vertices[2].remap(u1 / squishU + f, v2 / squishV - g);
            vertices[3] = vertices[3].remap(u2 / squishU - f, v2 / squishV - g);
            if (flip) {
                int i = vertices.length;

                for (int j = 0; j < i / 2; j++) {
                    Vertex vertex = vertices[j];
                    vertices[j] = vertices[i - 1 - j];
                    vertices[i - 1 - j] = vertex;
                }
            }

            this.direction = direction.getUnitVector();
            if (flip) {
                this.direction.mul(-1.0F, 1.0F, 1.0F);
            }
        }
    }

    // See ModelPart#Vertex
    @Environment(EnvType.CLIENT)
    public static class Vertex {
        public final Vector3f pos;
        public final float u;
        public final float v;

        public Vertex(float x, float y, float z, float u, float v) {
            this(new Vector3f(x, y, z), u, v);
        }

        public Vertex remap(float u, float v) {
            return new Vertex(this.pos, u, v);
        }

        public Vertex(Vector3f pos, float u, float v) {
            this.pos = pos;
            this.u = u;
            this.v = v;
        }
    }

    // See ModelPart#renderCuboids()
    private void renderQuads(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        for (Quad quad : this.quads) {
            renderQuad(quad, entry, vertexConsumer, light, overlay, color);
        }
    }

    // See ModelPart#renderCuboid()
    private void renderQuad(Quad quad, MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        Matrix4f matrix4f = entry.getPositionMatrix();
        Vector3f vector3f = new Vector3f();

        Vector3f transformedNormal = entry.transformNormal(quad.direction, vector3f);
        float f = transformedNormal.x();
        float g = transformedNormal.y();
        float h = transformedNormal.z();

        for (Vertex vertex : quad.vertices) {
            float i = vertex.pos.x() / 16.0F;
            float j = vertex.pos.y() / 16.0F;
            float k = vertex.pos.z() / 16.0F;
            Vector3f transformedPos = matrix4f.transformPosition(i, j, k, vector3f);
            vertexConsumer.vertex(transformedPos.x(), transformedPos.y(), transformedPos.z(), color, vertex.u, vertex.v, overlay, light, f, g, h);
        }
    }
}