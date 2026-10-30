package io.redspace.ironsspellbooks.block.alchemist_cauldron;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.gui.overlays.ScreenTooltipOverlay;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class AlchemistCauldronRenderer implements BlockEntityRenderer<AlchemistCauldronTile, AlchemistCauldronRenderer.State> {
    private final ItemModelResolver itemModels;

    public AlchemistCauldronRenderer(BlockEntityRendererProvider.Context context) {
        itemModels = context.itemModelResolver();
    }

    @Override
    public State createRenderState() { return new State(); }

    @Override
    public void extractRenderState(AlchemistCauldronTile tile, State state, float partialTick,
                                   Vec3 cameraPosition, CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(tile, state, partialTick, cameraPosition, breakProgress);
        state.fluids.clear();
        state.items.clear();
        state.tooltip = List.of();
        if (!(tile.getLevel() instanceof net.minecraft.client.multiplayer.ClientLevel level)) return;
        var minecraft = Minecraft.getInstance();
        int amount = tile.getFluidAmount();
        float waterOffset = Mth.lerp(Mth.clamp(amount, 0, 1000) / 1000f, .25f, .9f);
        float time = amount > 0 ? level.getGameTime() + partialTick : 15;
        for (int index = 0; index < tile.inputItems.size(); index++) {
            var stack = tile.inputItems.get(index);
            if (stack.isEmpty()) continue;
            var item = new ItemStackRenderState();
            itemModels.updateForTopItem(item, stack.copy(), ItemDisplayContext.FIXED, level, null, (int) tile.getBlockPos().asLong());
            Vec2 offset = getFloatingItemOffset(time, index * 587);
            state.items.add(new FloatingItem(item, offset.x, waterOffset + index * .01f, offset.y,
                    (time + index * 213) / (index + 1) * 1.5f));
        }
        var fluids = tile.fluidInventory.fluids();
        float running = amount;
        float height = waterOffset;
        for (FluidStack fluid : fluids) {
            var fluidState = fluid.getFluid().defaultFluidState();
            var model = minecraft.getModelManager().getFluidStateModelSet().get(fluidState);
            var sprite = model.stillMaterial().sprite();
            int tint = model.tintSource() == null ? 0xFFFFFF
                    : model.tintSource().colorInWorld(fluidState.createLegacyBlock(), level, tile.getBlockPos());
            Identifier id = BuiltInRegistries.FLUID.getKey(fluid.getFluid());
            if (id.getNamespace().equals(IronsSpellbooks.MODID)) {
                Identifier texture = switch (id.getPath()) {
                    case "blood", "timeless_slurry" -> Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "block/" + id.getPath());
                    default -> Identifier.withDefaultNamespace("block/water_still");
                };
                sprite = minecraft.getAtlasManager().getAtlasOrThrow(net.minecraft.data.AtlasIds.BLOCKS).getSprite(texture);
                tint = modFluidTint(fluid, id.getPath());
            }
            int emission = fluidState.createLegacyBlock().getLightEmission();
            int fluidLight = state.lightCoords & 0xF00000 | Math.max(state.lightCoords >> 4 & 15, emission) << 4;
            state.fluids.add(new FluidLayer(RenderTypes.entityTranslucent(sprite.atlasLocation()), height,
                    sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), tint,
                    amount > 0 ? running / amount : 0, fluidLight));
            running -= fluid.getAmount();
            height += .0005f;
        }
        var player = minecraft.player;
        if (player != null && Math.abs(player.getX() - tile.getBlockPos().getX()) < 5
                && Math.abs(player.getY() - tile.getBlockPos().getY()) < 5
                && Math.abs(player.getZ() - tile.getBlockPos().getZ()) < 5
                && player.isCrouching() && minecraft.hitResult instanceof BlockHitResult hit
                && hit.getBlockPos().equals(tile.getBlockPos())) {
            var tooltip = new ArrayList<Component>();
            tooltip.add(Component.translatable("block.irons_spellbooks.alchemist_cauldron").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.WHITE));
            if (fluids.isEmpty()) tooltip.add(Component.translatable("ui.irons_spellbooks.empty").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            for (int index = fluids.size() - 1; index >= 0; index--) {
                var fluid = fluids.get(index);
                var id = BuiltInRegistries.FLUID.getKey(fluid.getFluid());
                String key = id.getNamespace().equals(IronsSpellbooks.MODID) ? "fluid_type." + id.getNamespace() + "." + id.getPath()
                        : "block." + id.getNamespace() + "." + id.getPath().replace("flowing_", "");
                tooltip.add(Component.literal("  ").append(Component.translatable(key).withStyle(ChatFormatting.DARK_AQUA))
                        .append(": ").append(Component.literal(fluid.getAmount() + "mb").withStyle(ChatFormatting.GOLD)));
            }
            state.tooltip = List.copyOf(tooltip);
        }
    }

    @Override
    public void submit(State state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        for (FluidLayer layer : state.fluids) {
            collector.submitCustomGeometry(poses, layer.type, (pose, consumer) -> {
                float padding = 1 / 16f;
                float red = ((layer.tint >> 16) & 255) / 255f;
                float green = ((layer.tint >> 8) & 255) / 255f;
                float blue = (layer.tint & 255) / 255f;
                consumer.addVertex(pose.pose(), 1 - padding, layer.height, padding).setColor(red, green, blue, layer.opacity).setUv(layer.u1, layer.v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(layer.light).setNormal(0, 1, 0);
                consumer.addVertex(pose.pose(), padding, layer.height, padding).setColor(red, green, blue, layer.opacity).setUv(layer.u0, layer.v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(layer.light).setNormal(0, 1, 0);
                consumer.addVertex(pose.pose(), padding, layer.height, 1 - padding).setColor(red, green, blue, layer.opacity).setUv(layer.u0, layer.v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(layer.light).setNormal(0, 1, 0);
                consumer.addVertex(pose.pose(), 1 - padding, layer.height, 1 - padding).setColor(red, green, blue, layer.opacity).setUv(layer.u1, layer.v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(layer.light).setNormal(0, 1, 0);
            });
        }
        for (FloatingItem item : state.items) {
            poses.pushPose();
            try {
                poses.translate(item.x, item.y, item.z);
                poses.rotateDegrees(Axis.YP, item.rotation);
                poses.rotateDegrees(Axis.XP, 90);
                poses.scale(.4f, .4f, .4f);
                item.item.submit(poses, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            } finally { poses.popPose(); }
        }
        if (!state.tooltip.isEmpty()) ScreenTooltipOverlay.renderTooltip(state.tooltip,
                (sw, sh, mx, my, tw, th) -> new Vector2i(sw / 2 + 30, sh / 2 - th / 2));
    }

    public static Vec2 getFloatingItemOffset(float time, int offset) {
        float xspeed = offset % 2 == 0 ? .0075f : .025f * (1 + (offset % 88) * .001f);
        float yspeed = offset % 2 == 0 ? .025f : .0075f * (1 + (offset % 88) * .001f);
        float x = (Math.abs(((time + offset) * xspeed % 2) - 1) + 1) / 2;
        float y = (Math.abs(((time + offset + 4356) * yspeed % 2) - 1) + 1) / 2;
        return new Vec2(Mth.lerp(x, -.2f, .75f), Mth.lerp(y, -.2f, .75f));
    }

    private static int modFluidTint(FluidStack fluid, String name) {
        var potion = fluid.get(DataComponents.POTION_CONTENTS);
        if (potion != null) return potion.getColor();
        return switch (name) {
            case "blood" -> 0x8A1118;
            case "common_ink" -> 0x222222;
            case "uncommon_ink" -> 0x124300;
            case "rare_ink" -> 0x0F3844;
            case "epic_ink" -> 0xA52EA0;
            case "legendary_ink" -> 0xFCAC1C;
            case "oakskin_elixir", "greater_oakskin_elixir" -> 0xFFEF95;
            case "evasion_elixir", "greater_evasion_elixir" -> 0xF17BF4;
            case "invisibility_elixir", "greater_invisibility_elixir" -> 0x7F83F2;
            case "greater_healing_elixir" -> 0xF82423;
            case "ice_venom" -> 0x73BABA;
            default -> 0xFFFFFF;
        };
    }

    public static class State extends BlockEntityRenderState {
        public final List<FluidLayer> fluids = new ArrayList<>();
        public final List<FloatingItem> items = new ArrayList<>();
        public List<Component> tooltip = List.of();
    }

    public record FluidLayer(RenderType type, float height, float u0, float u1, float v0, float v1,
                             int tint, float opacity, int light) {}
    public record FloatingItem(ItemStackRenderState item, float x, float y, float z, float rotation) {}
}
