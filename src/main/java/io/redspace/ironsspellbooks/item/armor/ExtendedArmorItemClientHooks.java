package io.redspace.ironsspellbooks.item.armor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.renderer.GeoArmorRenderer;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public final class ExtendedArmorItemClientHooks {
    private ExtendedArmorItemClientHooks() {
    }

    public static void createGeoRenderer(ExtendedArmorItem armorItem, Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?, ?> renderer;

            @Override
            public GeoArmorRenderer<?, ?> getGeoArmorRenderer(ItemStack itemStack, EquipmentSlot equipmentSlot) {
                if (this.renderer == null) {
                    this.renderer = armorItem.supplyRenderer();
                }
                return this.renderer;
            }
        });
    }
}
