package io.redspace.ironslib.transmog.client;

import com.geckolib.animatable.GeoItem;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.GeoArmorRenderer.ArmorSegment;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironslib.transmog.TransmogItemData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class TransmogArmorRenderer<T extends Item & GeoItem, R extends HumanoidRenderState> extends GeoArmorRenderer<T, R> {
   protected final ArrayList<TransmogArmorRenderer.AsyncBone> asyncBones;
   protected final ArrayList<TransmogArmorRenderer.CapeBone> capeBones;
   private boolean hideHat;
   private boolean hideJacket;
   private boolean dyeable;
   private int defaultDyeColor = -1;

   public TransmogArmorRenderer(GeoModel model) {
      super(model);
      this.asyncBones = new ArrayList<>();
      this.asyncBones.add(new TransmogArmorRenderer.AsyncBone("armorLeggingTorsoLayer", EquipmentSlot.LEGS, ArmorSegment.CHEST, Vec3.ZERO));
      this.asyncBones
         .add(new TransmogArmorRenderer.AsyncBone("armorTorsoExtensionRightLeg", EquipmentSlot.CHEST, ArmorSegment.RIGHT_LEG, new Vec3(2.0, 12.0, 0.0)));
      this.asyncBones
         .add(new TransmogArmorRenderer.AsyncBone("armorTorsoExtensionLeftLeg", EquipmentSlot.CHEST, ArmorSegment.LEFT_LEG, new Vec3(-2.0, 12.0, 0.0)));
      this.capeBones = new ArrayList<>();
   }

   public boolean hideHat() {
      return this.hideHat;
   }

   public boolean hideJacket() {
      return this.hideJacket;
   }

   public TransmogArmorRenderer<T, R> setHideHat() {
      this.hideHat = true;
      return this;
   }

   public TransmogArmorRenderer<T, R> setHideJacket() {
      this.hideJacket = true;
      return this;
   }

   public TransmogArmorRenderer<T, R> setDyeable() {
      this.dyeable = true;
      return this;
   }

   public TransmogArmorRenderer<T, R> setDyeable(int defaultDyeColor) {
      this.dyeable = true;
      this.defaultDyeColor = defaultDyeColor;
      return this;
   }

   public TransmogArmorRenderer<T, R> withCapeBone(String bone) {
      this.capeBones.add(new TransmogArmorRenderer.CapeBone(bone, new Vec3(0.5, 0.0, 0.0)));
      return this;
   }

   public TransmogArmorRenderer<T, R> withCapeBone(TransmogArmorRenderer.CapeBone... bones) {
      this.capeBones.addAll(List.of(bones));
      return this;
   }

   public void preRenderPass(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
      var renderState = (R)renderPassInfo.renderState();
      EquipmentSlot slot = Objects.requireNonNull((EquipmentSlot)renderState.getGeckolibData(CURRENT_SLOT));
      HumanoidModel baseModel = Objects.requireNonNull((HumanoidModel)renderState.getGeckolibData(BASE_MODEL));
      renderPassInfo.addBoneUpdater((info, snapshots) -> this.adjustBonesForSlot(info, snapshots, slot, baseModel));
   }

   public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
   }

   @Override
   public RenderType getRenderType(R state, Identifier texture) {
      return state.getOrDefaultGeckolibData(DataTickets.HAS_GLINT, false)
         ? RenderTypes.armorCutoutNoCullGlint(texture) : RenderTypes.armorCutoutNoCull(texture);
   }

   public void submitRenderTasks(RenderPassInfo<R> renderPassInfo, OrderedSubmitNodeCollector renderTasks, @Nullable RenderType renderType) {
      if (renderType != null) {
         if (renderPassInfo.model().isMissingno()) {
            this.submitMissingModelRender(renderPassInfo, renderTasks);
         } else {
            var renderState = (R)renderPassInfo.renderState();
            EquipmentSlot slot = Objects.requireNonNull((EquipmentSlot)renderState.getGeckolibData(CURRENT_SLOT));
            Set<String> topLevelBones = this.getBonesForEquipmentSlot(renderState, slot).keySet();
            int packedLight = renderPassInfo.packedLight();
            int packedOverlay = renderPassInfo.packedOverlay();
            int renderColor = renderPassInfo.renderColor();
            int dyeColor = this.dyeable ? this.resolveDyeColor(renderState, slot) : renderColor;
            this.submitBoneGeometry(renderPassInfo, renderTasks, renderType, topLevelBones, packedLight, packedOverlay, renderColor, dyeColor, this.dyeable);
         }
      }
   }

   private void submitBoneGeometry(
      RenderPassInfo<R> renderPassInfo,
      OrderedSubmitNodeCollector renderTasks,
      RenderType renderType,
      Set<String> topLevelBones,
      int packedLight,
      int packedOverlay,
      int renderColor,
      int dyeColor,
      boolean applyDye
   ) {
      renderTasks.submitCustomGeometry(renderPassInfo.poseStack(), renderType, (pose, vertexConsumer) -> {
         PoseStack poseStack = renderPassInfo.poseStack();
         poseStack.pushPose();
         try {
            poseStack.last().set(pose);
            renderPassInfo.renderPosed(() -> {
               if (applyDye) {
                  this.renderTopLevelBones(renderPassInfo, vertexConsumer, topLevelBones, packedLight, packedOverlay, ARGB.multiply(renderColor, dyeColor), true);
                  this.renderTopLevelBones(renderPassInfo, vertexConsumer, topLevelBones, packedLight, packedOverlay, renderColor, false);
               } else {
                  this.renderTopLevelBones(renderPassInfo, vertexConsumer, topLevelBones, packedLight, packedOverlay, renderColor, null);
               }
            });
         } finally {
            poseStack.popPose();
         }
      });
   }

   private void renderTopLevelBones(
      RenderPassInfo<R> renderPassInfo,
      VertexConsumer vertexConsumer,
      Set<String> topLevelBones,
      int packedLight,
      int packedOverlay,
      int color,
      @Nullable Boolean renderDyed
   ) {
      for (String boneName : topLevelBones) {
         renderPassInfo.model().getBone(boneName).ifPresent(bone -> {
            this.applyDyeVisibility(bone, renderDyed);
            bone.positionAndRender(renderPassInfo, vertexConsumer, packedLight, packedOverlay, color);
         });
      }
   }

   private void adjustBonesForSlot(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots, EquipmentSlot slot, HumanoidModel baseModel) {
      R renderState = (R)renderPassInfo.renderState();
      baseModel.setupAnim(renderState);

      for (ArmorSegment segment : this.getSegmentsForSlot(renderState, slot)) {
         this.applySegmentSnapshot(renderPassInfo, snapshots, baseModel, this.getBoneNameForSegment(renderState, segment), segment);
      }

      for (TransmogArmorRenderer.AsyncBone bone : this.asyncBones) {
         if (bone.itemSlot.equals(slot)) {
            snapshots.get(bone.boneName)
               .ifPresent(
                  snapshot -> {
                     ModelPart modelPart = (ModelPart)bone.segment.modelPartGetter.apply(baseModel);
                     snapshot.setRotX(-modelPart.xRot)
                        .setRotY(-modelPart.yRot)
                        .setRotZ(modelPart.zRot)
                        .setTranslateX((float)bone.partOffset.x + modelPart.x)
                        .setTranslateY((float)bone.partOffset.y + -modelPart.y)
                        .setTranslateZ((float)bone.partOffset.z + modelPart.z);
                     if (this.dyeable) {
                        renderPassInfo.model().getBone(bone.boneName).ifPresent(geoBone -> this.ensureChildSnapshots(snapshots, geoBone));
                     }
                  }
               );
         }
      }

      if (!this.capeBones.isEmpty()) {
         CapeData capeData = ((ICapeDataProvider)renderState).irons_lib$getCapeData();

         for (TransmogArmorRenderer.CapeBone capeBone : this.capeBones) {
            snapshots.get(capeBone.name())
               .ifPresent(
                  snapshot -> snapshot.setRotX((float)(capeData.capeLean * (float) (-Math.PI / 180.0) * capeBone.rotationMultipliers.x))
                     .setRotY((float)(capeData.capeFlap * (float) (-Math.PI / 180.0) * capeBone.rotationMultipliers.y))
                     .setRotZ((float)(capeData.capeLean2 * (float) (Math.PI / 180.0) * capeBone.rotationMultipliers.z))
               );
         }
      }
   }

   public Map<String, ArmorSegment> getBonesForEquipmentSlot(R renderState, EquipmentSlot slot) {
      HashMap<String, ArmorSegment> bones = new HashMap<>();
      this.getSegmentsForSlot(renderState, slot).forEach(segment -> bones.put(this.getBoneNameForSegment(renderState, segment), segment));

      for (TransmogArmorRenderer.AsyncBone bone : this.asyncBones) {
         if (bone.itemSlot.equals(slot)) {
            bones.put(bone.boneName, bone.segment);
         }
      }

      return bones;
   }

   public Map<String, ArmorSegment> getAllBones(R renderState) {
      HashMap<String, ArmorSegment> bones = new HashMap<>();

      for (EquipmentSlot slot : ARMOR_SLOTS) {
         this.getSegmentsForSlot(renderState, slot).forEach(segment -> bones.put(this.getBoneNameForSegment(renderState, segment), segment));
      }

      for (TransmogArmorRenderer.AsyncBone bone : this.asyncBones) {
         bones.put(bone.boneName, bone.segment);
      }

      return bones;
   }

   private void applySegmentSnapshot(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots, HumanoidModel baseModel, String boneName, ArmorSegment segment) {
      snapshots.get(boneName)
         .ifPresent(
            snapshot -> {
               ModelPart modelPart = (ModelPart)segment.modelPartGetter.apply(baseModel);
               Vector3f bonePos = segment.modelPartMatcher.apply(new Vector3f(modelPart.x, modelPart.y, modelPart.z));
               snapshot.setRotX(-modelPart.xRot)
                  .setRotY(-modelPart.yRot)
                  .setRotZ(modelPart.zRot)
                  .setTranslateX(bonePos.x)
                  .setTranslateY(bonePos.y)
                  .setTranslateZ(bonePos.z);
               if (this.dyeable) {
                  renderPassInfo.model().getBone(boneName).ifPresent(bone -> this.ensureChildSnapshots(snapshots, bone));
               }
            }
         );
   }

   private int resolveDyeColor(R renderState, EquipmentSlot slot) {
      ItemStack stack = this.stackForSlot(renderState, slot);
      return TransmogItemData.has(stack) ? TransmogItemData.get(stack).dyeColor() | 0xFF000000 : this.defaultDyeColor;
   }

   private @NonNull ItemStack stackForSlot(R renderState, EquipmentSlot slot) {
      return switch (slot) {
         case FEET -> renderState.feetEquipment;
         case LEGS -> renderState.legsEquipment;
         case CHEST -> renderState.chestEquipment;
         case HEAD -> renderState.headEquipment;
         default -> ItemStack.EMPTY;
      };
   }

   private void ensureChildSnapshots(BoneSnapshots snapshots, GeoBone parent) {
      for (GeoBone child : parent.children()) {
         snapshots.get(child);
         this.ensureChildSnapshots(snapshots, child);
      }
   }

   private void applyDyeVisibility(GeoBone bone, @Nullable Boolean renderDyed) {
      if (bone.frameSnapshot != null) {
         boolean isDyeBone = bone.name().startsWith("dye");
         bone.frameSnapshot.skipRender(renderDyed != null && isDyeBone != renderDyed);
      }

      for (GeoBone child : bone.children()) {
         this.applyDyeVisibility(child, renderDyed);
      }
   }

   public static class AsyncBone {
      private final String boneName;
      private final EquipmentSlot itemSlot;
      private final ArmorSegment segment;
      private final Vec3 partOffset;

      public AsyncBone(String boneName, EquipmentSlot itemSlot, ArmorSegment segment, Vec3 partOffset) {
         this.boneName = boneName;
         this.itemSlot = itemSlot;
         this.segment = segment;
         this.partOffset = partOffset;
      }
   }

   public record CapeBone(String name, Vec3 rotationMultipliers) {
   }
}
