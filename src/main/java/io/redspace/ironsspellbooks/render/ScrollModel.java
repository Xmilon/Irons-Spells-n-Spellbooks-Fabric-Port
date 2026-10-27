package io.redspace.ironsspellbooks.render;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.util.ScrollSchoolTag;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class ScrollModel extends NBTOverrideItemModel {
    public ScrollModel(BakedModel original) {
        super(original);
    }

    @Override
    Optional<Identifier> getModelFromStack(ItemStack itemStack) {
        var schoolFromTag = ScrollSchoolTag.getSchool(itemStack);
        if (schoolFromTag.isPresent()) {
            var schoolIdText = schoolFromTag.get();
            var schoolId = Identifier.tryParse(schoolIdText);
            if (schoolId != null) {
                return Optional.of(Identifier.fromNamespaceAndPath(schoolId.getNamespace(), String.format("item/scroll_%s", schoolId.getPath())));
            }
            return Optional.of(getScrollModelLocation(schoolIdText));
        }
        if (ISpellContainer.isSpellContainer(itemStack)) {
            var spell = ISpellContainer.get(itemStack).getSpellAtIndex(0).getSpell();
            if (spell != SpellRegistry.none()) {
                return Optional.of(getScrollModelLocation(spell.getSchoolType()));
            }
        }
        return Optional.empty();
    }

    public static Identifier getScrollModelLocation(SchoolType schoolType) {
        return getScrollModelLocation(schoolType.getId().getPath());
    }

    public static Identifier getScrollModelLocation(String schoolPath) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, String.format("item/scroll_%s", schoolPath));
    }
}
