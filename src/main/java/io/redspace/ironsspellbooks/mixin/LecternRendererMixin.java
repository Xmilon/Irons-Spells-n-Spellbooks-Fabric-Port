package io.redspace.ironsspellbooks.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.item.ILecternPlaceable;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.render.LecternBook;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.LecternRenderer;
import net.minecraft.client.renderer.blockentity.state.LecternRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LecternRenderer.class)
public class LecternRendererMixin {
    @Shadow
    private BookModel bookModel;

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/level/block/entity/LecternBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/LecternRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
            at = @At("TAIL"))
    private void irons_spellbooks$captureBook(LecternBlockEntity blockEntity, LecternRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumbling, CallbackInfo ci) {
        LecternBook book = null;
        ItemStack stack = blockEntity.getBook();
        if (blockEntity.getBlockState().getValue(LecternBlock.HAS_BOOK) && stack.getItem() instanceof ILecternPlaceable placeable) {
            var textureOverride = placeable.simpleTextureOverride(stack);
            if (textureOverride.isPresent()) {
                book = new LecternBook(placeable, textureOverride, null);
            } else if (stack.getItem() instanceof SpellBook || stack.is(ItemRegistry.THE_CHRONICLE.get())/*todo: item tag for "3d lecterns"*/) {
                var item = new ItemStackRenderState();
                Minecraft.getInstance().getItemModelResolver().updateForTopItem(item, stack, ItemDisplayContext.HEAD, blockEntity.getLevel(), null, 0);
                book = new LecternBook(placeable, textureOverride, item);
            } else {
                // our book, but nothing of ours to draw: vanilla must not draw its own book either
                book = new LecternBook(placeable, textureOverride, null);
            }
        }
        ((FabricRenderState) state).setData(LecternBook.KEY, book);
    }

    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/blockentity/state/LecternRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void irons_spellbooks$submit(LecternRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        LecternBook book = ((FabricRenderState) state).getData(LecternBook.KEY);
        if (book == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.0625F, 0.5F);
        // state.yRot is the book's facing, already in the orientation vanilla applies
        poseStack.rotateDegrees(Axis.YP, -state.yRot);
        poseStack.rotateDegrees(Axis.ZP, 67.5F);
        if (book.textureOverride().isPresent()) {
            poseStack.translate(0.0F, -0.125F, 0.0F);
            collector.submitModel(this.bookModel, BookModel.State.forAnimation(0.0F, 0.1F, 0.9F, 1.2F), poseStack, RenderTypes.entitySolid(book.textureOverride().get()), state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        } else if (book.item() != null) {
            poseStack.rotateDegrees(Axis.XP, -90f);
            poseStack.rotateDegrees(Axis.ZP, 90f);
            poseStack.rotateDegrees(Axis.YP, 180f);
            poseStack.translate(0.125F, -0.625F, 0.125F);
            book.placeable().handleCustomLecternPosing(poseStack);
            book.item().submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        }
        poseStack.popPose();
        ci.cancel();
    }
}
