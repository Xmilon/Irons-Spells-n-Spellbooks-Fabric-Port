package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.block.pedestal.PedestalRenderer;
import io.redspace.ironsspellbooks.block.scroll_forge.ScrollForgeRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public final class BlockItemRenderSnapshotCheck {
    public static void main(String[] args) {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
        // A fixture supplies an empty baked layer; the real resolver still sets context and clears reused state.
        var resolver = new ItemModelResolver(null) {
            @Override public void appendItemLayers(ItemStackRenderState output, ItemStack stack,
                    ItemDisplayContext context, Level level, ItemOwner owner, int seed) {
                output.newLayer();
            }
        };
        var paperStack = new ItemStack(net.minecraft.core.Holder.direct(Items.PAPER, net.minecraft.core.component.DataComponentMap.EMPTY));
        var context = new BlockEntityRendererProvider.Context(null, null, resolver, null, null, null, null, null);
        var items = new ArrayList<Matrix4f>();
        var paper = new ArrayList<SubmitNodeCollector.CustomGeometryRenderer>();
        var paperPoses = new ArrayList<PoseStack.Pose>();
        SubmitNodeCollector collector = (SubmitNodeCollector) Proxy.newProxyInstance(
                SubmitNodeCollector.class.getClassLoader(), new Class<?>[]{SubmitNodeCollector.class}, (proxy, method, values) -> {
                    if (method.getName().equals("submitItem")) {
                        items.add(new Matrix4f(((PoseStack) values[0]).last().pose()));
                        assert (int) values[2] == 12345 : "Captured item lighting changed";
                    } else if (method.getName().equals("submitCustomGeometry")) {
                        paper.add((SubmitNodeCollector.CustomGeometryRenderer) values[2]);
                        paperPoses.add(((PoseStack) values[0]).last().copy());
                    }
                    return null;
                });
        var pedestal = new PedestalRenderer(context);
        var state = pedestal.createRenderState();
        var poses = new PoseStack();
        pedestal.submit(state, poses, collector, null);
        assert items.isEmpty() : "Empty pedestal drew an item";
        resolver.updateForTopItem(state.item, paperStack, ItemDisplayContext.FIXED, null, null, 0);
        state.lightCoords = 12345;
        state.rotation = 36;
        pedestal.submit(state, poses, collector, null);
        state.tilt = true;
        pedestal.submit(state, poses, collector, null);
        assert items.size() == 2 && !items.get(0).equals(items.get(1)) : "Sword/tool tilt lost";
        assert poses.last().pose().equals(new Matrix4f()) : "Pedestal leaked its transform";
        resolver.updateForTopItem(state.item, ItemStack.EMPTY, ItemDisplayContext.FIXED, null, null, 0);
        pedestal.submit(state, poses, collector, null);
        assert items.size() == 2 : "Removed item remained in reused state";

        var forge = new ScrollForgeRenderer(context);
        var forgeState = forge.createRenderState();
        forgeState.lightCoords = 12345;
        forge.submit(forgeState, poses, collector, null);
        assert paper.isEmpty() : "Empty forge drew paper";
        forgeState.paper = true;
        resolver.updateForTopItem(forgeState.ink, paperStack, ItemDisplayContext.FIXED, null, null, 0);
        resolver.updateForTopItem(forgeState.focus, paperStack, ItemDisplayContext.FIXED, null, null, 0);
        for (var facing : List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)) {
            forgeState.facing = facing;
            forge.submit(forgeState, poses, collector, null);
            assert poses.last().pose().equals(new Matrix4f()) : "Forge leaked its transform";
        }
        assert paper.size() == 4 && items.size() == 10 : "Forge item/paper submissions changed";
        for (int i = 1; i < 4; i++) assert !paperPoses.get(0).pose().equals(paperPoses.get(i).pose()) : "Forge facing ignored";
        forgeState.lightCoords = 0;
        forgeState.paper = false;
        var uvs = new ArrayList<List<Object>>();
        var lights = new ArrayList<Integer>();
        VertexConsumer vertices = (VertexConsumer) Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),
                new Class<?>[]{VertexConsumer.class}, (proxy, method, values) -> {
                    if (method.getName().equals("setUv")) uvs.add(List.of(values));
                    if (method.getName().equals("setLight")) lights.add((int) values[0]);
                    return method.getReturnType() == VertexConsumer.class ? proxy : null;
                });
        paper.getFirst().render(paperPoses.getFirst(), vertices);
        assert uvs.equals(List.of(List.of(0f, 1f), List.of(0f, 0f), List.of(1f, 0f), List.of(1f, 1f))) : "Paper UV winding changed";
        assert lights.equals(List.of(12345, 12345, 12345, 12345)) : "Deferred paper read changed state";
        for (var type : List.of(PedestalRenderer.State.class, ScrollForgeRenderer.State.class)) {
            for (var field : type.getDeclaredFields()) {
                assert !ItemStack.class.isAssignableFrom(field.getType()) : "Live stack retained";
                assert !Level.class.isAssignableFrom(field.getType()) : "Live world retained";
                assert !net.minecraft.world.level.block.entity.BlockEntity.class.isAssignableFrom(field.getType()) : "Live tile retained";
            }
        }
        System.out.println("Block item snapshots: empty/reused states, tool tilt, four facings, paper UV/light and balanced poses passed");
    }
}
