package me.oop.oxygen.util.coll;

import com.oop.memorystore.api.Store;
import com.oop.memorystore.implementation.index.IndexDefinition;
import com.oop.memorystore.implementation.memory.MemoryStore;
import com.oop.memorystore.implementation.query.Query;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.NonNull;
import me.oop.oxygen.util.ClassUtility;

public class ClassBasedStorage<T> {
    protected final Store<T> storage;
    protected final Predicate<T> newValueFilter;
    private final Function<T, List<Class<?>>> indexCreator;

    public ClassBasedStorage() {
        this((Predicate<T>) null);
    }

    public ClassBasedStorage(Predicate<T> newValueFilter) {
        this(newValueFilter, null);
    }

    public ClassBasedStorage(Function<T, List<Class<?>>> indexCreator) {
        this(null, indexCreator);
    }

    public ClassBasedStorage(Predicate<T> newValueFilter, Function<T, List<Class<?>>> indexCreator) {
        this.newValueFilter = newValueFilter;
        this.indexCreator = Optional.ofNullable(indexCreator).orElse((object) -> ClassUtility.getSupersAndItself(object.getClass()));
        this.storage = this.createStorage();
    }

    private Store<T> createStorage() {
        final Store<T> store = new MemoryStore<>();
        store.index(
            "class",
            IndexDefinition.withKeyMappings(this.indexCreator::apply)
        );

        return store;
    }

    public void put(@NonNull T object) {
        if (!this.filter(object)) {
            throw new IllegalStateException(String.format("Storage %s cannot accept %s object", this.getClass(), object.getClass()));
        }

        this.storage.add(object);
    }

    public Store<T> getStorage() {
        return this.storage;
    }

    private boolean filter(T object) {
        if (this.newValueFilter == null) {
            return true;
        }

        return this.newValueFilter.test(object);
    }

    public <TYPE extends T> TYPE get(@NonNull Class<TYPE> clazz) {
        return this.find(clazz).orElseThrow(() -> new IllegalStateException(String.format("Failed to find stored object by class %s", clazz)));
    }

    public <TYPE extends T> Optional<TYPE> find(@NonNull Class<TYPE> clazz) {
        return this.storage.findFirst(Query.where("class", clazz)).map((object) -> (TYPE) object);
    }

    public <TYPE extends T> TYPE getOrSupply(@NonNull Class<TYPE> clazz, @NonNull Supplier<TYPE> supplier) {
        return this.find(clazz).orElseGet(() -> {
            final TYPE supplied = supplier.get();
            this.put(supplied);

            return supplied;
        });
    }
}
