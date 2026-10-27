package io.redspace.ironsspellbooks.item.armor;

import com.geckolib.animation.state.AnimationTest;

import com.geckolib.animation.object.PlayState;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.AnimationController;

import com.google.common.base.Suppliers;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ExtendedArmorItem extends Item implements GeoItem {
    private final ArmorType armorType;

    public ExtendedArmorItem(ArmorMaterial pMaterial, ArmorType pType, Properties pProperties, AttributeContainer... attributes) {
        super(pProperties.humanoidArmor(pMaterial, pType).attributes(createArmorAttributes(pMaterial, pType, attributes)));
        this.armorType = pType;
    }

    public ArmorType getArmorType() {
        return armorType;
    }

    private static ItemAttributeModifiers createArmorAttributes(ArmorMaterial material, ArmorType type, AttributeContainer... attributes) {
        int armor = material.defense().getOrDefault(type, 0);
        float toughness = material.toughness();
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(type.getSlot());
        Identifier id = Identifier.withDefaultNamespace("armor." + type.getName());
        builder.add(
                Attributes.ARMOR, new AttributeModifier(id, armor, AttributeModifier.Operation.ADD_VALUE), slotGroup
        );
        builder.add(
                Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id, toughness, AttributeModifier.Operation.ADD_VALUE), slotGroup
        );
        float knockbackResistance = material.knockbackResistance();
        if (knockbackResistance > 0.0F) {
            builder.add(
                    Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(id, knockbackResistance, AttributeModifier.Operation.ADD_VALUE),
                    slotGroup
            );
        }
        for (AttributeContainer holder : attributes) {
            builder.add(holder.attribute(), holder.createModifier(type.getSlot().getName()), slotGroup);
        }
        return builder.build();
    }

    public static AttributeContainer[] schoolAttributes(Holder<Attribute> school) {
        return new AttributeContainer[]{new AttributeContainer(AttributeRegistry.MAX_MANA, 125, AttributeModifier.Operation.ADD_VALUE), new AttributeContainer(school, 0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)};
    }

    public static AttributeContainer[] withManaAttribute(int mana) {
        return new AttributeContainer[]{new AttributeContainer(AttributeRegistry.MAX_MANA, mana, AttributeModifier.Operation.ADD_VALUE)};
    }

    public static AttributeContainer[] withManaAndSpellPowerAttribute(int mana, double spellPower) {
        return new AttributeContainer[]{new AttributeContainer(AttributeRegistry.MAX_MANA, mana, AttributeModifier.Operation.ADD_VALUE), new AttributeContainer(AttributeRegistry.SPELL_POWER, spellPower, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)};
    }



    /*
    GECKOLIB IMPL
     */
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>("controller", 20, this::predicate));
    }

    private PlayState predicate(AnimationTest<ExtendedArmorItem> extendedArmorItemAnimationState) {
        extendedArmorItemAnimationState.controller().setAnimation(RawAnimation.begin().thenLoop("idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        if (FabricLoader.getInstance().getEnvironmentType() != net.fabricmc.api.EnvType.CLIENT) {
            return;
        }
        ExtendedArmorItemClientHooks.createGeoRenderer(this, consumer);
    }

    @OnlyIn(Dist.CLIENT)
    public abstract GeoArmorRenderer<?, ?> supplyRenderer();
}
