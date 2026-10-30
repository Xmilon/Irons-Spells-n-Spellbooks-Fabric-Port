package io.redspace.ironsspellbooks.fluids;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.fluids.FluidStack;

public class PotionClientFluidType extends SimpleClientFluidType {
    public PotionClientFluidType(Identifier texture) {
        super(texture);
    }

    @Override
    public int getTintColor(FluidStack stack) {
        return (stack.has(DataComponents.POTION_CONTENTS) ? stack.get(DataComponents.POTION_CONTENTS).getColor() : new PotionContents(Potions.WATER).getColor()) | 0xFF000000; // force full ARGB alpha ('or' on alpha component)
    }

}
