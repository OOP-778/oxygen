package me.oop.oxygen.entity;

import com.oop.memorystore.implementation.query.Query;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.NonNull;
import me.oop.oxygen.proxy.ProxyClassWrapper;
import me.oop.oxygen.util.TypeAndParams;
import me.oop.oxygen.util.TypedFactory;
import me.oop.oxygen.util.coll.ClassBasedStorage;
import org.jetbrains.annotations.Nullable;

public class EntityFactory extends TypedFactory<ProxyClassWrapper<?>> {
    private final ClassBasedStorage<ProxyClassWrapper<?>> createdClassWrappers = new ClassBasedStorage<>(
        (Function<ProxyClassWrapper<?>, List<Class<?>>>) (object) -> List.of(object.getProxiedClass(), object.getOriginalClass())
    );

    public <T> T createInstance(Class<T> clazz, @Nullable EntityController entityController) {
        return this.createInstance(TypeAndParams.of(clazz), entityController);
    }

    public <T> T createInstance(@NonNull TypeAndParams<T> typeAndParams, @Nullable EntityController entityController) {
        if (typeAndParams.getRoot().isInterface()) {
            throw new IllegalStateException(String.format("Cannot create instance of an interface: %s", typeAndParams.getRoot()));
        }

        final Optional<ProxyClassWrapper<?>> inMemoryWrapper = this.createdClassWrappers.getStorage().findFirst(Query.where("class", typeAndParams.getRoot()));
        if (!inMemoryWrapper.isPresent()) {
            final FactoryEntry<T> tFactoryEntry = this.get(typeAndParams);

            final ProxyClassWrapper<?> newWrapper = (ProxyClassWrapper<?>) tFactoryEntry.create(typeAndParams.getRoot());
            this.createdClassWrappers.put(newWrapper);

            return (T) newWrapper.newInstance(entityController);
        }

        return (T) inMemoryWrapper.get().newInstance(entityController);
    }

}
