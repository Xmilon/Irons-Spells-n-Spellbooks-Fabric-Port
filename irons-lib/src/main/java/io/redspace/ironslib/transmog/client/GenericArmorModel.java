package io.redspace.ironslib.transmog.client;

import com.geckolib.animatable.GeoItem;
import com.geckolib.model.DefaultedItemGeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

public class GenericArmorModel<T extends Item & GeoItem> extends DefaultedItemGeoModel<T> {
   private final Identifier model;
   private final Identifier texture;
   private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath("irons_lib", "wizard_armor_animation");

   public GenericArmorModel(String modid, String name) {
      this(
         Identifier.fromNamespaceAndPath(modid, String.format("%s_armor", name)),
         Identifier.fromNamespaceAndPath(modid, String.format("textures/models/armor/%s.png", name))
      );
   }

   public GenericArmorModel(Identifier model, Identifier texture) {
      super(Identifier.fromNamespaceAndPath(model.getNamespace(), ""));
      this.model = model;
      this.texture = texture;
   }

   public GenericArmorModel(String name) {
      this("irons_lib", name);
   }

   public @NonNull Identifier getModelResource(@NonNull GeoRenderState renderState) {
      return this.model;
   }

   public @NonNull Identifier getTextureResource(@NonNull GeoRenderState renderState) {
      return this.texture;
   }

   public @NonNull Identifier getAnimationResource(T animatable) {
      return ANIMATION;
   }
}
