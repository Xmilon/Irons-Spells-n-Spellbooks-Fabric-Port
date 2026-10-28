package io.redspace.ironsspellbooks.particle;

import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.particle.SingleQuadParticle.Layer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.oit.OitPipelineSet;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;

public final class ParticleRenderLayers {
    private static final RenderPipeline ADDITIVE_PIPELINE = RenderPipeline.builder(RenderPipelines.PARTICLE_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("irons_spellbooks", "pipeline/additive_particle"))
            .withCull(false)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withColorTargetState(new ColorTargetState(new BlendFunction(
                    BlendFactor.ONE, BlendFactor.ONE, BlendFactor.ONE, BlendFactor.ONE)))
            .build();
    private static final OitPipelineSet ADDITIVE_OIT = OitPipelineSet.builder("irons_spellbooks/additive_particle",
            RenderPipeline.builder(RenderPipelines.OIT_PARTICLE_SNIPPET)
                    .withCull(false).withShaderDefine("OIT_ADDITIVE")).build();

    public static final Layer ADDITIVE = new Layer(true, TextureAtlas.LOCATION_PARTICLES, ADDITIVE_PIPELINE, ADDITIVE_OIT);

    private ParticleRenderLayers() { }
}
