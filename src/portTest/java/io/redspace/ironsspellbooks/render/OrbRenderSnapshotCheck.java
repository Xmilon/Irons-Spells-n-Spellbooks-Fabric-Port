package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.entity.spells.acid_orb.AcidOrbRenderer;
import io.redspace.ironsspellbooks.entity.spells.magma_ball.MagmaBallRenderer;
import io.redspace.ironsspellbooks.entity.spells.snowball.SnowballRenderer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import org.joml.Matrix4f;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public final class OrbRenderSnapshotCheck {
    private record Draw(ModelPart model, Matrix4f pose, RenderType type, int light) { }

    public static void main(String[] args) {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
        var geometry = AcidOrbRenderer.createBodyLayer().bakeRoot();
        var context = new EntityRendererProvider.Context(null, null, null, null, null, null, null, null, null, null, null) {
            @Override public ModelPart bakeLayer(ModelLayerLocation location) { return geometry; }
        };
        var draws = new ArrayList<Draw>();
        SubmitNodeCollector collector = (SubmitNodeCollector) Proxy.newProxyInstance(SubmitNodeCollector.class.getClassLoader(),
                new Class<?>[]{SubmitNodeCollector.class}, (proxy, method, values) -> {
                    if (method.getName().equals("submitModelPart")) draws.add(new Draw((ModelPart) values[0],
                            new Matrix4f(((PoseStack) values[1]).last().pose()), (RenderType) values[2], (int) values[3]));
                    return null;
                });
        var poses = new PoseStack();
        var acid = new AcidOrbRenderer(context);
        var acidState = acid.createRenderState();
        acidState.texture = Identifier.parse("irons_spellbooks:textures/entity/acid_orb/acid_orb.png");
        acidState.swirlTexture = Identifier.parse("irons_spellbooks:textures/entity/acid_orb/swirl_3.png");
        acidState.centerHeight = .35;
        acidState.yaw = 25;
        acidState.pitch = -17;
        acidState.ageInTicks = 12.5f;
        acidState.lightCoords = 12345;
        acid.submit(acidState, poses, collector, null);
        acidState.ageInTicks = 30;
        acid.submit(acidState, poses, collector, null);
        assert draws.size() == 4;
        assert draws.get(0).pose.equals(draws.get(2).pose) : "Acid animation moved core";
        assert !draws.get(1).pose.equals(draws.get(3).pose) : "Acid swirl stopped animating";
        assert draws.stream().allMatch(draw -> draw.light == 12345) : "Acid lighting lost";
        assert !draws.get(0).type.equals(draws.get(1).type) : "Acid texture layers collapsed";
        var capturedPose = new Matrix4f(draws.get(1).pose);
        acidState.pitch = 90;
        acidState.swirlTexture = acidState.texture;
        assert draws.get(1).pose.equals(capturedPose) : "Submitted pose retained mutable state";
        assert poses.last().pose().equals(new Matrix4f()) : "Acid transform leaked";
        draws.clear();

        var magma = new MagmaBallRenderer(context);
        var magmaState = magma.createRenderState();
        magmaState.texture = Identifier.parse("irons_spellbooks:textures/entity/fireball/magma.png");
        magmaState.swirlTexture = Identifier.parse("irons_spellbooks:textures/entity/fireball/swirl_4.png");
        magmaState.centerHeight = .4;
        magmaState.ageInTicks = 12.5f;
        magmaState.lightCoords = 12345;
        magma.submit(magmaState, poses, collector, null);
        magmaState.ageInTicks = 30;
        magma.submit(magmaState, poses, collector, null);
        assert draws.size() == 4;
        assert draws.get(0).light == LightCoordsUtil.FULL_BRIGHT && draws.get(1).light == 12345 : "Magma core/swirl lighting changed";
        assert !draws.get(0).pose.equals(draws.get(2).pose) && !draws.get(1).pose.equals(draws.get(3).pose) : "Magma animation lost";
        assert poses.last().pose().equals(new Matrix4f()) : "Magma transform leaked";
        draws.clear();

        var snow = new SnowballRenderer(context);
        var snowState = snow.createRenderState();
        snowState.texture = Identifier.withDefaultNamespace("textures/block/snow.png");
        snowState.centerHeight = .2;
        snowState.ageInTicks = 12.5f;
        snowState.lightCoords = 12345;
        snow.submit(snowState, poses, collector, null);
        snowState.ageInTicks = 30;
        snow.submit(snowState, poses, collector, null);
        assert draws.size() == 2 && !draws.get(0).pose.equals(draws.get(1).pose) : "Snowball animation lost";
        assert draws.stream().allMatch(draw -> draw.light == 12345) : "Snowball lighting lost";
        assert poses.last().pose().equals(new Matrix4f()) : "Snowball transform leaked";
        for (var part : geometry.getAllParts()) {
            assert part.xRot == 0 && part.yRot == 0 && part.zRot == 0 : "Shared model rotated during submission";
        }
        for (var type : List.of(AcidOrbRenderer.State.class, MagmaBallRenderer.State.class, SnowballRenderer.State.class)) {
            for (var field : type.getDeclaredFields()) {
                assert !net.minecraft.world.entity.Entity.class.isAssignableFrom(field.getType()) : "Live entity retained in orb state";
                assert !net.minecraft.world.level.Level.class.isAssignableFrom(field.getType()) : "Live world retained in orb state";
            }
        }
        System.out.println("Native orb snapshots: acid/magma/snow animation, light, textures, pose isolation and shared geometry passed");
    }
}
