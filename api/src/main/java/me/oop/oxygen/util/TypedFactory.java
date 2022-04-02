package me.oop.oxygen.util;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public class TypedFactory<T> {
    private final LinkedList<FactoryEntry<?>> entries = new LinkedList<>();

    public <TYPE> FactoryEntry<TYPE> get(@NonNull TypeAndParams<TYPE> typeAndParams) {
        return this.find(typeAndParams).orElseThrow(() -> new IllegalStateException(String.format("Failed to find entry for %s", typeAndParams)));
    }

    public <T> Optional<FactoryEntry<T>> find(@NonNull TypeAndParams<T> typeAndParams) {
        for (FactoryEntry entry : this.entries) {
            if (!entry.filter.test(typeAndParams)) {
                continue;
            }

            return Optional.of((FactoryEntry<T>) entry);
        }

        return Optional.empty();
    }

    public <TYPE> void register(@NonNull Class<TYPE> rootClass, @NonNull UnaryOperator<FactoryEntryBuilder<T>> builder) {
        final FactoryEntryBuilder<T> factoryEntryBuilder = new FactoryEntryBuilder<>();
        builder.apply(factoryEntryBuilder);

        Objects.requireNonNull(factoryEntryBuilder.entry, "Registering factory cannot be null");

        switch (factoryEntryBuilder.method) {
            case ADD:
                this.entries.add(factoryEntryBuilder.entry);
                break;
            case AFTER:
                this.entries.add(this.entries.indexOf(factoryEntryBuilder.with) + 1, factoryEntryBuilder.entry);
                break;
            case BEFORE:
                this.entries.add(this.entries.indexOf(factoryEntryBuilder.with), factoryEntryBuilder.entry);
                break;
            case REPLACE:
                this.entries.set(this.entries.indexOf(factoryEntryBuilder.with), factoryEntryBuilder.entry);
        }
    }

    public enum InsertMethod {
        REPLACE,
        AFTER,
        ADD,
        BEFORE
    }

    public static class FactoryEntryBuilder<T> {
        private FactoryEntry<T> entry;
        private InsertMethod method = InsertMethod.ADD;
        private FactoryEntry<?> with;

        public FactoryEntryBuilder<T> registering(@NonNull Predicate<TypeAndParams> filter, @NonNull Function<TypeAndParams, T> maker) {
            return this.registering(new FactoryEntry<>(maker, filter));
        }

        public FactoryEntryBuilder<T> registering(@NonNull FactoryEntry<T> entry) {
            this.entry = entry;
            return this;
        }

        public FactoryEntryBuilder<T> before(@NonNull FactoryEntry<?> entry) {
            return this.method(InsertMethod.BEFORE, entry);
        }

        public FactoryEntryBuilder<T> method(@NonNull InsertMethod method, @Nullable FactoryEntry<?> with) {
            this.method = method;
            this.with = with;
            return this;
        }

        public FactoryEntryBuilder<T> after(@NonNull FactoryEntry<?> entry) {
            return this.method(InsertMethod.AFTER, entry);
        }

        public FactoryEntryBuilder<T> replace(@NonNull FactoryEntry<?> entry) {
            return this.method(InsertMethod.REPLACE, entry);
        }
    }

    @FunctionalInterface
    public interface Creator<T> {
        Object create(Class<T> clazz);
    }

    public static class FactoryEntry<T> {
        private final Function<TypeAndParams, T> maker;
        private final Predicate<TypeAndParams> filter;

        public FactoryEntry(Function<TypeAndParams, T> maker, Predicate<TypeAndParams> filter) {
            this.maker = maker;
            this.filter = filter;
        }

        public Object create(@NonNull Class<?> toFactory) {
            return this.maker.apply(TypeAndParams.of(toFactory));
        }
    }
}
