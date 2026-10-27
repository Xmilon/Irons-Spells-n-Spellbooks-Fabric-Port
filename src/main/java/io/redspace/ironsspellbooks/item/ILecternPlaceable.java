package io.redspace.ironsspellbooks.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public interface ILecternPlaceable {
    List<Component> getPages(ItemStack stack);

    default Optional<Identifier> simpleTextureOverride(ItemStack stack){
        return Optional.empty();
    }

    default void handleCustomLecternPosing(PoseStack poseStack){

    }
}
