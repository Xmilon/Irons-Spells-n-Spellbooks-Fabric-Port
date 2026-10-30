package net.neoforged.neoforge.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

/** Lazy registration supplier for integrations not yet migrated to native registration. */
public class DeferredHolder<R, T extends R> implements Supplier<T> {
    private final Identifier id;
    private final ResourceKey<? extends Registry<R>> registryKey;
    private final Supplier<T> supplier;
    private T value;

    public DeferredHolder(ResourceKey<? extends Registry<R>> registryKey, Identifier id, Supplier<T> supplier) {
        this.registryKey = registryKey;
        this.id = id;
        this.supplier = supplier;
    }

    public static <R, T extends R> DeferredHolder<R, T> create(ResourceKey<? extends Registry<R>> registryKey, Identifier id) {
        return new DeferredHolder<>(registryKey, id, () -> null);
    }

    public String getId() { return id.getPath(); }
    public Identifier getLocation() { return id; }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        if (value == null) {
            Registry<R> registry = (Registry<R>) BuiltInRegistries.REGISTRY.getValue(registryKey.identifier());
            R registered = registry != null && registry.containsKey(id) ? registry.getValue(id) : null;
            value = registered != null ? (T) registered : supplier.get();
        }
        return value;
    }

    /** Native registry holder for this entry; only valid once the entry has been registered. */
    @SuppressWarnings("unchecked")
    public Holder<R> holder() {
        Registry<R> registry = (Registry<R>) BuiltInRegistries.REGISTRY.getValue(registryKey.identifier());
        return registry.get(id).orElseThrow();
    }
}
