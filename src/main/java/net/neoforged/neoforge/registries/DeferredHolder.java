package net.neoforged.neoforge.registries;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DeferredHolder<R, T extends R> implements Supplier<T>, Holder<R> {
    private final Identifier id;
    private final ResourceKey<? extends Registry<R>> registryKey;
    private ResourceKey<R> key;
    private final Supplier<T> supplier;
    private T value;

    public DeferredHolder(ResourceKey<? extends Registry<R>> registryKey, Identifier id, Supplier<T> supplier) {
        this.registryKey = registryKey;
        this.id = id;
        this.supplier = supplier;
    }

    public static <R, T extends R> DeferredHolder<R, T> create(ResourceKey<? extends net.minecraft.core.Registry<R>> registryKey, Identifier id) {
        DeferredHolder<R, T> holder = new DeferredHolder<>(registryKey, id, () -> null);
        holder.key = ResourceKey.create(registryKey, id);
        return holder;
    }

    public String getId() {
        return id.getPath();
    }

    public Identifier getLocation() {
        return id;
    }

    public void bindKey(ResourceKey<R> key) {
        this.key = key;
    }

    @SuppressWarnings("unchecked")
    private Optional<Registry<R>> resolveRegistry() {
        Registry<? extends Registry<?>> root = BuiltInRegistries.REGISTRY;
        Registry<R> registry = (Registry<R>) root.get(registryKey.identifier());
        return Optional.ofNullable(registry);
    }

    private Optional<Holder.Reference<R>> resolveReference() {
        if (key == null) {
            return Optional.empty();
        }
        return resolveRegistry().flatMap(registry -> registry.get(key).map(holder -> (Holder.Reference<R>) holder));
    }

    private Optional<R> resolveRegisteredValue() {
        return resolveRegistry().flatMap(registry -> registry.containsKey(id) ? Optional.ofNullable(registry.getValue(id)) : Optional.empty());
    }

    @Override
    public T get() {
        if (value == null) {
            value = (T) resolveRegisteredValue().orElseGet(supplier);
        }
        return value;
    }

    public boolean isPresent() {
        return true;
    }

    @Override
    public R value() {
        return resolveReference().map(Holder::value).orElseGet(this::get);
    }

    @Override
    public boolean isBound() {
        return resolveReference().isPresent() || get() != null;
    }

    @Override
    public boolean is(Identifier location) {
        return id.equals(location) || (key != null && key.identifier().equals(location));
    }

    @Override
    public boolean is(ResourceKey<R> resourceKey) {
        return key != null && key.equals(resourceKey);
    }

    @Override
    public boolean is(Predicate<ResourceKey<R>> predicate) {
        return unwrapKey().filter(predicate).isPresent();
    }

    @Override
    public boolean is(TagKey<R> tagKey) {
        return resolveReference().map(holder -> holder.is(tagKey)).orElse(false);
    }

    @Override
    public boolean is(Holder<R> holder) {
        if (holder == this) {
            return true;
        }
        return this.unwrapKey().isPresent() && this.unwrapKey().equals(holder.unwrapKey());
    }

    @Override
    public Stream<TagKey<R>> tags() {
        return resolveReference().map(Holder.Reference::tags).orElseGet(Stream::empty);
    }

    @Override
    public Either<ResourceKey<R>, R> unwrap() {
        return resolveReference().map(Holder::unwrap).orElseGet(() -> key != null ? Either.left(key) : Either.right(get()));
    }

    @Override
    public Optional<ResourceKey<R>> unwrapKey() {
        return resolveReference().flatMap(Holder::unwrapKey).or(() -> Optional.ofNullable(key));
    }

    @Override
    public Kind kind() {
        return resolveReference().map(Holder::kind).orElse(key == null ? Kind.DIRECT : Kind.REFERENCE);
    }

    @Override
    public boolean canSerializeIn(HolderOwner<R> owner) {
        return resolveReference().map(holder -> holder.canSerializeIn(owner)).orElse(key != null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Holder<?> other)) {
            return false;
        }
        return unwrapKey().isPresent() && unwrapKey().equals(other.unwrapKey());
    }

    // Matches Holder.Reference's identity hash so registry holders and this holder find each other in hash maps.
    @Override
    public int hashCode() {
        return resolveReference().map(System::identityHashCode).orElseGet(() -> System.identityHashCode(this));
    }
}
