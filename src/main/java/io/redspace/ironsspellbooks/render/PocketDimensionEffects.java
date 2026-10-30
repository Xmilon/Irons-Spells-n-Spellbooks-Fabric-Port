package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Random;

/** Pocket sky geometry is captured once; GPU drawing only consumes that immutable snapshot. */
public final class PocketDimensionEffects {
    public static final RenderStateDataKey<Snapshot> SKY = RenderStateDataKey.create(() -> "irons_spellbooks:pocket_sky");
    private static final Identifier DIMENSION = Identifier.parse("irons_spellbooks:pocket_dimension");
    public static final Identifier SKY_LOCATION = Identifier.parse("irons_spellbooks:textures/environment/pocket_dimension_sky.png");
    public static final Identifier CLOUDS_LOCATION = Identifier.parse("irons_spellbooks:textures/environment/pocket_clouds.png");
    public static final Identifier WISP_LOCATION = Identifier.parse("irons_spellbooks:textures/environment/single_cloud.png");
    public static final Identifier NOISE = Identifier.parse("irons_spellbooks:textures/environment/noise_tile.png");

    private PocketDimensionEffects() {}

    public record Vertex(float x, float y, float z, float u, float v, int color) {}
    public record Batch(Identifier texture, boolean additive, boolean depthTest,
                        float red, float green, float blue, List<Vertex> vertices) {
        public Batch { vertices = List.copyOf(vertices); }
    }
    public record Snapshot(List<Batch> batches) {
        public Snapshot { batches = List.copyOf(batches); }
    }

    public static Snapshot capture(ClientLevel level, float partialTick, Camera camera) {
        if (!level.dimension().identifier().equals(DIMENSION)) return null;
        return capture(level.getGameTime() + partialTick, camera.getViewRotationMatrix(new Matrix4f()), camera.position());
    }

    public static Snapshot capture(float time, Matrix4f viewRotation, Vec3 cameraPosition) {
        var batches = new ArrayList<Batch>();
        PoseStack poses = new PoseStack();
        poses.mulPose(new Matrix4f(viewRotation));
        var vertices = new ArrayList<Vertex>();
        box(poses, vertices, 100, 1, 0xFF454545);
        batches.add(new Batch(SKY_LOCATION, false, true, 1, 1, 1, vertices));

        float scale = .80f;
        Random random = new Random(431);
        for (int i = 0; i < 6; i++) {
            poses.pushPose();
            float speed = (.01f + i * i * .09f) * .015f;
            poses.rotate(Axis.XP.rotationDegrees((i * 68731 + time * speed * (random.nextFloat() - .5f)) % 360));
            poses.rotate(Axis.YP.rotationDegrees((i * 74869 + time * speed * (random.nextFloat() - .5f)) % 360));
            poses.rotate(Axis.ZP.rotationDegrees((i * 98744 + time * speed * (random.nextFloat() - .5f)) % 360));
            var tint = new Vector3f(random.nextFloat() * .5f + .5f, random.nextFloat() * .5f + .5f, random.nextFloat() * .5f + .5f);
            tint.mul(Mth.lerp((5 - i) / 6f, .25f, .8f));
            vertices = new ArrayList<>();
            box(poses, vertices, 100 * scale, 4f + 2f * scale, 0xFF808080);
            batches.add(new Batch(CLOUDS_LOCATION, true, true, tint.x, tint.y, tint.z, vertices));
            poses.popPose();
            scale -= .04f;
        }

        float offset = 0;
        offset = nebula(poses, batches, new Vector3f(.1f, .4f, .6f).mul(.075f), random, time, scale, offset);
        offset = nebula(poses, batches, new Vector3f(.6f, .1f, .5f).mul(.125f), random, time, scale, offset);
        nebula(poses, batches, new Vector3f(.3f, .3f, .3f).mul(.125f), random, time, scale, offset);

        // Preserve the server's saved pocket spacing and the original negative-coordinate truncation.
        int traversal = (int) (cameraPosition.z / 256) * 256;
        PoseStack aura = new PoseStack();
        aura.mulPose(new Matrix4f(viewRotation).translate((float) -cameraPosition.x, (float) -cameraPosition.y, (float) -cameraPosition.z));
        aura.translate(7.5f, 1, 7.5f + traversal);
        vertices = new ArrayList<>();
        float minV = (time / 20 / 12) % 1;
        float maxV = minV + 5f / 20 / 12;
        for (int i = 0; i < 4; i++) {
            aura.pushPose();
            aura.rotate(Axis.YP.rotationDegrees(i * 90));
            vertex(aura, vertices, -3.5f, 0, 3.5f, 0, maxV, 0xFF9911AA);
            vertex(aura, vertices, -3.5f, 3, 3.5f, 0, minV, 0xFF000000);
            vertex(aura, vertices, 3.5f, 3, 3.5f, 2, minV, 0xFF000000);
            vertex(aura, vertices, 3.5f, 0, 3.5f, 2, maxV, 0xFF9911AA);
            aura.popPose();
        }
        batches.add(new Batch(NOISE, true, false, 1, 1, 1, vertices));
        return new Snapshot(batches);
    }

    private static float nebula(PoseStack poses, List<Batch> batches, Vector3f color, Random random,
                                float time, float scale, float offset) {
        var vertices = new ArrayList<Vertex>();
        for (int i = 0; i < 15; i++) {
            poses.pushPose();
            poses.rotate(Axis.XP.rotationDegrees((random.nextInt(360) + time * .005f) % 360));
            poses.rotate(Axis.YP.rotationDegrees((random.nextInt(360) + time * .005f) % 360));
            poses.rotate(Axis.ZP.rotationDegrees((random.nextInt(360) + time * .005f) % 360));
            for (int j = 0; j <= i; j++) {
                var displacement = new Vector3f(random.nextFloat() - .5f, 0, random.nextFloat() - .5f).mul(25 * (1 + j * .025f));
                poses.pushPose();
                poses.translate(displacement.x, offset, displacement.z);
                float size = 100 * scale * (.15f + i * .003f);
                plane(poses, vertices, size, 100 * scale, 1, 0xFF606060);
                poses.popPose();
                offset += .03f;
            }
            poses.popPose();
        }
        batches.add(new Batch(WISP_LOCATION, true, true, color.x, color.y, color.z, vertices));
        return offset;
    }

    private static void box(PoseStack poses, List<Vertex> vertices, float distance, float maxUV, int color) {
        for (int i = 0; i < 6; i++) {
            poses.pushPose();
            switch (i) {
                case 1 -> poses.rotate(Axis.XP.rotationDegrees(90));
                case 2 -> poses.rotate(Axis.XP.rotationDegrees(-90));
                case 3 -> poses.rotate(Axis.XP.rotationDegrees(180));
                case 4 -> poses.rotate(Axis.ZP.rotationDegrees(90));
                case 5 -> poses.rotate(Axis.ZP.rotationDegrees(-90));
            }
            plane(poses, vertices, distance, distance, maxUV, color);
            poses.popPose();
        }
    }

    private static void plane(PoseStack poses, List<Vertex> vertices, float extent, float distance, float maxUV, int color) {
        vertex(poses, vertices, -extent, -distance, -extent, 0, 0, color);
        vertex(poses, vertices, -extent, -distance, extent, 0, maxUV, color);
        vertex(poses, vertices, extent, -distance, extent, maxUV, maxUV, color);
        vertex(poses, vertices, extent, -distance, -extent, maxUV, 0, color);
    }

    private static void vertex(PoseStack poses, List<Vertex> vertices, float x, float y, float z, float u, float v, int color) {
        var position = poses.last().pose().transformPosition(new Vector3f(x, y, z));
        vertices.add(new Vertex(position.x, position.y, position.z, u, v, color));
    }

    public static void draw(Snapshot snapshot, GpuBufferSlice fog, RenderTarget target) {
        RenderSystem.setShaderFog(fog);
        for (Batch batch : snapshot.batches()) {
            try (var storage = new ByteBufferBuilder(batch.vertices().size() * DefaultVertexFormat.POSITION_TEX_COLOR.getVertexSize())) {
                var builder = new BufferBuilder(storage, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                for (Vertex vertex : batch.vertices()) {
                    builder.addVertex(vertex.x(), vertex.y(), vertex.z()).setUv(vertex.u(), vertex.v()).setColor(vertex.color());
                }
                try (var mesh = builder.buildOrThrow();
                     var buffer = RenderSystem.getDevice().createBuffer(() -> "Pocket dimension sky", GpuBuffer.USAGE_VERTEX, mesh.vertexBuffer());
                     var pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Pocket dimension sky",
                             target.getColorTextureView(), Optional.empty(), target.getDepthTextureView(), OptionalDouble.empty())) {
                    RenderPipeline pipeline = !batch.depthTest() ? Pipelines.AURA : batch.additive() ? Pipelines.ADDITIVE : Pipelines.SKY;
                    pass.setPipeline(RenderSystem.getCompiledPipeline(pipeline));
                    RenderSystem.bindDefaultUniforms(pass);
                    pass.setUniform("DynamicTransforms", RenderSystem.getDynamicUniforms().writeTransform(new Matrix4f(), new Vector4f(batch.red(), batch.green(), batch.blue(), 1)));
                    var texture = Minecraft.getInstance().getTextureManager().getTexture(batch.texture());
                    pass.setUniform("Sampler0", texture.getTextureView(), texture.getSampler());
                    pass.setVertexBuffer(0, buffer.slice());
                    var indices = RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS);
                    pass.setIndexBuffer(indices.getBuffer(mesh.drawState().indexCount()), indices.type());
                    pass.drawIndexed(mesh.drawState().indexCount(), 1, 0, 0, 0);
                }
            }
        }
    }

    private static final class Pipelines {
        static final RenderPipeline SKY = pipeline("sky", false, true);
        static final RenderPipeline ADDITIVE = pipeline("sky_additive", true, true);
        static final RenderPipeline AURA = pipeline("sky_border_aura", true, false);

        private static RenderPipeline pipeline(String name, boolean additive, boolean depth) {
            return RenderPipeline.builder().withLocation(Identifier.fromNamespaceAndPath("irons_spellbooks", "pipeline/" + name))
                    .withVertexShader("core/position_tex_color").withFragmentShader("core/position_tex_color")
                    .withBindGroupLayout(BindGroupLayouts.GLOBALS).withBindGroupLayout(BindGroupLayouts.PROJECTION)
                    .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withBindGroupLayout(BindGroupLayouts.SAMPLER0)
                    .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR).withPrimitiveTopology(PrimitiveTopology.QUADS)
                    .withCull(true).withColorTargetState(new ColorTargetState(additive ? BlendFunction.ADDITIVE : BlendFunction.TRANSLUCENT))
                    .withDepthStencilState(depth ? Optional.of(DepthStencilState.DEFAULT) : Optional.empty()).build();
        }
    }
}
