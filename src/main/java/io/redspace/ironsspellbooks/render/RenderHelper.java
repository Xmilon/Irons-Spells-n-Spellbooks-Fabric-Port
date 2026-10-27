package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.oit.OitPipelineSet;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix3x2fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RenderHelper {
    public static int colorLerp(float f, int colorA, int colorB) {
        int redA = (colorA >> 16) & 0xFF;
        int greenA = (colorA >> 8) & 0xFF;
        int blueA = colorA & 0xFF;
        int redB = (colorB >> 16) & 0xFF;
        int greenB = (colorB >> 8) & 0xFF;
        int blueB = colorB & 0xFF;
        return color255(
                (int) Mth.lerp(f, redA, redB),
                (int) Mth.lerp(f, greenA, greenB),
                (int) Mth.lerp(f, blueA, blueB)
        );
    }

    public static int color255(int pRed, int pGreen, int pBlue, int pAlpha) {
        return pAlpha << 24 | pRed << 16 | pGreen << 8 | pBlue;
    }

    public static int color255(int pRed, int pGreen, int pBlue) {
        return color255(pRed, pGreen, pBlue, 255);
    }

    public static int colorf(float pRed, float pGreen, float pBlue, float pAlpha) {
        return color255((int) (255 * pRed), (int) (255 * pGreen), (int) (255 * pBlue), (int) (255 * pAlpha));
    }

    public static int colorf(float pRed, float pGreen, float pBlue) {
        return colorf(pRed, pGreen, pBlue, 1f);
    }

    public static QuadBuilder quadBuilder() {
        return new QuadBuilder();
    }

    public static class QuadBuilder {
        List<Vector3f> verticies;
        List<Vector3f> normals;
        List<Vec2> uvs;
        List<Integer> colors;
        Integer light = null;
        Integer overlay = null;
        @Nullable Matrix4f matrix;

        private QuadBuilder() {
            this.verticies = new ArrayList<>();
            this.normals = new ArrayList<>();
            this.uvs = new ArrayList<>();
            this.colors = new IntArrayList();
        }

        public QuadBuilder vertex(float x, float y) {
            this.verticies.add(new Vector3f(x, y, 0));
            return this;
        }

        public QuadBuilder vertex(float x, float y, float z) {
            this.verticies.add(new Vector3f(x, y, z));
            return this;
        }

        public QuadBuilder uv(float u, float v) {
            this.uvs.add(new Vec2(u, v));
            return this;
        }


        public QuadBuilder normal(float x, float y, float z) {
            this.normals.add(new Vector3f(x, y, z));
            return this;
        }

        public QuadBuilder matrix(Matrix4f matrix) {
            this.matrix = matrix;
            return this;
        }

        /**
         * Color in the format 0-1f, rgba
         */
        public QuadBuilder color(Vector4f color) {
            this.colors.add(colorf(color.x, color.y, color.z, color.w));
            return this;
        }

        public QuadBuilder color(int color) {
            this.colors.add(color);
            return this;
        }

        /**
         * Color in the format 0-1f, rgb
         */
        public QuadBuilder color(Vector3f color) {
            return color(new Vector4f(color.x, color.y, color.z, 1f));
        }

        /**
         * Color in the format 0-1f, rgb
         */
        public QuadBuilder color(float r, float g, float b) {
            return color(r, g, b, 1f);
        }

        /**
         * Color in the format 0-1f, rgba
         */
        public QuadBuilder color(float r, float g, float b, float a) {
            return color(new Vector4f(r, g, b, a));
        }

        public QuadBuilder light(int light) {
            this.light = light;
            return this;
        }

        public QuadBuilder overlay(int overlay) {
            this.overlay = overlay;
            return this;
        }

        public void build(VertexConsumer consumer) {
            for (int i = 0; i < verticies.size(); i++) {
                var vertex = verticies.get(i);
                int color;
                if (colors.isEmpty()) {
                    color = 0xFFFFFF;
                } else if (colors.size() == 1 || colors.size() != verticies.size()) {
                    color = colors.get(0);
                } else {
                    color = colors.get(i);
                }
                if (matrix != null) {
                    vertex = matrix.transformPosition(vertex.x, vertex.y, vertex.z, new Vector3f());
                }
                consumer.addVertex(vertex.x, vertex.y, vertex.z).setColor(color);
                if (!uvs.isEmpty()) {
                    consumer.setUv(uvs.get(i).x, uvs.get(i).y);
                }
                if (!normals.isEmpty()) {
                    consumer.setNormal(normals.get(i).x, normals.get(i).y, normals.get(i).z);
                }
                if (light != null) {
                    consumer.setLight(light);
                }
                if (overlay != null) {
                    consumer.setOverlay(overlay);
                }
            }
        }

        public void build(GuiGraphicsExtractor graphics, RenderType renderType) {
            var textures = renderType.state.prepareTextures(Minecraft.getInstance().getTextureManager(),
                    RenderSystem.getSamplerCache(), null, null);
            GpuTextureView[] views = new GpuTextureView[3];
            GpuSampler[] samplers = new GpuSampler[3];
            for (var texture : textures) {
                int index = switch (texture.name()) {
                    case "Sampler0" -> 0;
                    case "Sampler1" -> 1;
                    case "Sampler2" -> 2;
                    default -> throw new IllegalArgumentException("Unsupported GUI sampler: " + texture.name());
                };
                views[index] = texture.textureView();
                samplers[index] = texture.sampler();
            }
            build(graphics, renderType.pipeline(), new TextureSetup(views[0], views[1], views[2],
                    samplers[0], samplers[1], samplers[2]));
        }

        public void build(GuiGraphicsExtractor graphics, RenderPipeline pipeline) {
            build(graphics, pipeline, TextureSetup.noTexture());
        }

        public void build(GuiGraphicsExtractor graphics) {
            build(graphics, RenderPipelines.GUI);
        }

        private void build(GuiGraphicsExtractor graphics, RenderPipeline pipeline, TextureSetup textures) {
            if (verticies.isEmpty()) {
                return;
            }
            if (verticies.size() % 4 != 0 || !uvs.isEmpty() && uvs.size() != verticies.size()
                    || !normals.isEmpty() && normals.size() != verticies.size()) {
                throw new IllegalStateException("GUI quads need four vertices and matching UV/normal counts");
            }
            List<GuiQuadRenderState.Vertex> vertices = new ArrayList<>(verticies.size());
            Matrix3x2fc pose = graphics.pose();
            for (int i = 0; i < verticies.size(); i++) {
                Vector3f vertex = new Vector3f(verticies.get(i));
                if (matrix != null) {
                    matrix.transformPosition(vertex);
                }
                float x = pose.m00() * vertex.x + pose.m10() * vertex.y + pose.m20();
                float y = pose.m01() * vertex.x + pose.m11() * vertex.y + pose.m21();
                int color = colors.isEmpty() ? 0xFFFFFFFF : colors.get(colors.size() == verticies.size() ? i : 0);
                Vec2 uv = uvs.isEmpty() ? Vec2.ZERO : uvs.get(i);
                Vector3f normal = normals.isEmpty() ? new Vector3f() : normals.get(i);
                vertices.add(new GuiQuadRenderState.Vertex(x, y, vertex.z, color,
                        uv.x, uv.y, normal.x, normal.y, normal.z));
            }
            graphics.guiRenderState.addGuiElement(new GuiQuadRenderState(pipeline, textures, vertices,
                    !uvs.isEmpty(), !normals.isEmpty(), light, overlay, graphics.scissorStack.peek()));
        }
    }

    public static class CustomerRenderType {
        private static final BlendFunction ONE_MINUS = new BlendFunction(BlendFactor.ONE_MINUS_SRC_ALPHA,
                BlendFactor.SRC_COLOR, BlendFactor.ONE, BlendFactor.ZERO);
        private static final RenderPipeline DARK_PORTAL_PIPELINE = pipeline("dark_portal", false, ONE_MINUS);
        private static final RenderPipeline MAGIC_PIPELINE = pipeline("magic_glow", true, BlendFunction.ADDITIVE);
        private static final RenderPipeline MAGIC_NO_CULL_PIPELINE = pipeline("magic_glow_no_cull", false, BlendFunction.ADDITIVE);
        private static final RenderPipeline PYRIUM_PIPELINE = pipeline("pyrium_staff_orb", true, null);
        private static final OitPipelineSet MAGIC_OIT = oit("magic_glow", true);
        private static final OitPipelineSet MAGIC_NO_CULL_OIT = oit("magic_glow_no_cull", false);

        private static RenderPipeline pipeline(String name, boolean cull, @Nullable BlendFunction blend) {
            return RenderPipeline.builder(RenderPipelines.ENERGY_SWIRL_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath("irons_spellbooks", "pipeline/" + name))
                    .withCull(cull)
                    .withColorTargetState(blend == null ? ColorTargetState.DEFAULT : new ColorTargetState(blend))
                    .withDepthStencilState(DepthStencilState.DEFAULT)
                    .build();
        }

        private static OitPipelineSet oit(String name, boolean cull) {
            return OitPipelineSet.builder("irons_spellbooks/" + name,
                    RenderPipeline.builder(RenderPipelines.ENERGY_SWIRL_SNIPPET)
                            .withCull(cull).withShaderDefine("OIT_ADDITIVE")).build();
        }

        private static RenderType create(String name, Identifier texture, RenderPipeline pipeline,
                                         @Nullable OitPipelineSet oit, float u, float v) {
            var setup = RenderSetup.builder(pipeline).withTexture("Sampler0", texture)
                    .useLightmap().useOverlay().sortOnUpload()
                    .setTextureTransform(new TextureTransform.OffsetTextureTransform(u, v));
            if (oit != null) {
                setup.setOitPipelines(oit);
            } else if (pipeline == DARK_PORTAL_PIPELINE) {
                // shortcut: destination-color dark portals use classic ordering; add custom accumulation when porting portal transparency.
                setup.withForcedSolidModelPhase();
            }
            return RenderType.create(name, setup.createRenderSetup());
        }

        public static @NotNull RenderType darkGlow(@NotNull Identifier pLocation) {
            return DARK_PORTAL_GLOW.apply(pLocation);
        }

        private static final Function<Identifier, RenderType> DARK_PORTAL_GLOW = Util.memoize(
                texture -> create("dark_portal", texture, DARK_PORTAL_PIPELINE, null, 0, 0)
        );
        private static final Function<Identifier, RenderType> MAGIC = Util.memoize(
                texture -> create("magic_glow", texture, MAGIC_PIPELINE, MAGIC_OIT, 0, 0)
        );
        private static final Function<Identifier, RenderType> MAGIC_NO_CULL = Util.memoize(
                texture -> create("magic_glow_no_cull", texture, MAGIC_NO_CULL_PIPELINE, MAGIC_NO_CULL_OIT, 0, 0)
        );

        public static final Function<Identifier, RenderType> PYRIUM_STAFF_ORB = Util.memoize(
                texture -> create("pyrium_staff_orb", texture, PYRIUM_PIPELINE, null, 0, 0)
        );

        public static RenderType magic(Identifier pLocation) {
            return MAGIC.apply(pLocation);
        }

        public static RenderType magicNoCull(Identifier pLocation) {
            return MAGIC_NO_CULL.apply(pLocation);
        }

        public static RenderType magicSwirl(Identifier pLocation, float pU, float pV) {
            return create("magic_glow_swirl", pLocation, MAGIC_PIPELINE, MAGIC_OIT, pU, pV);
        }


    }
}
