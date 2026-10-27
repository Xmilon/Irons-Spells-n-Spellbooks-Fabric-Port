package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface UpgradeType {

    Map<Identifier, UpgradeType> UPGRADE_REGISTRY = new HashMap<>();

    static void registerUpgrade(UpgradeType upgrade) {
        UPGRADE_REGISTRY.put(upgrade.getId(), upgrade);
    }

    static Optional<UpgradeType> getUpgrade(Identifier key) {
        UpgradeType upgradeType = UPGRADE_REGISTRY.get(key);
        return upgradeType == null ? Optional.empty() : Optional.of(upgradeType);
    }

    Holder<Attribute> getAttribute();

    AttributeModifier.Operation getOperation();

    float getAmountPerUpgrade();

    Identifier getId();

    Optional<Holder<Item>> getContainerItem();
}
