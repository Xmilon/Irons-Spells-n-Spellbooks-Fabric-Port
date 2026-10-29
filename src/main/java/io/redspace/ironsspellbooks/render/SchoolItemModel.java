package io.redspace.ironsspellbooks.render;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.util.ScrollSchoolTag;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record SchoolItemModel(Kind kind, Map<Identifier, ItemModel> schools, ItemModel fallback) implements ItemModel {
    public SchoolItemModel {
        schools = Map.copyOf(schools);
    }

    @Override
    public void update(ItemStackRenderState output, ItemStack item, ItemModelResolver resolver,
                       ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        output.appendModelIdentityElement(this);
        Identifier school = item.isEmpty() ? null : selectedSchool(item);
        (school == null ? fallback : schools.getOrDefault(school, fallback))
                .update(output, item, resolver, displayContext, level, owner, seed);
    }

    private @Nullable Identifier selectedSchool(ItemStack item) {
        if (kind == Kind.AFFINITY_RING) {
            if (!AffinityData.hasAffinityData(item)) return null;
            var spell = AffinityData.getAffinityData(item).getSpell();
            return spell == SpellRegistry.none() ? null : spell.getSchoolType().getId();
        }
        var tag = ScrollSchoolTag.getSchool(item);
        if (tag.isPresent()) {
            String value = tag.get();
            return Identifier.tryParse(value.indexOf(':') < 0 ? IronsSpellbooks.MODID + ":" + value : value);
        }
        if (!ISpellContainer.isSpellContainer(item)) return null;
        var container = ISpellContainer.get(item);
        if (container == null || container.isEmpty() || container.getAllSpells().length == 0) return null;
        var data = container.getSpellAtIndex(0);
        if (data == null || data.getSpell() == SpellRegistry.none()) return null;
        return data.getSpell().getSchoolType().getId();
    }

    public enum Kind implements StringRepresentable {
        SCROLL("scroll"), AFFINITY_RING("affinity_ring");

        public static final Codec<Kind> CODEC = StringRepresentable.fromEnum(Kind::values);
        private final String name;

        Kind(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        public Identifier modelLocation(Identifier school) {
            return Identifier.fromNamespaceAndPath(school.getNamespace(), "item/" + name + "_" + school.getPath());
        }
    }

    public record Unbaked(Kind kind, ItemModel.Unbaked fallback) implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Kind.CODEC.fieldOf("kind").forGetter(Unbaked::kind),
                ItemModels.CODEC.fieldOf("fallback").forGetter(Unbaked::fallback)
        ).apply(instance, Unbaked::new));

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
            for (var school : SchoolRegistry.REGISTRY) resolver.markDependency(kind.modelLocation(school.getId()));
            fallback.resolveDependencies(resolver);
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transformation) {
            var models = new HashMap<Identifier, ItemModel>();
            for (var school : SchoolRegistry.REGISTRY) {
                var child = new CuboidItemModelWrapper.Unbaked(kind.modelLocation(school.getId()), Optional.empty(), List.of());
                models.put(school.getId(), child.bake(context, transformation));
            }
            return new SchoolItemModel(kind, models, fallback.bake(context, transformation));
        }
    }
}
