package me.oop.oxygen.util;

import lombok.NonNull;
import lombok.ToString;

/**
 * Wrapper for java Type Root is the main class Params is the generic types inside Ex. Map<String, String> ^ root ^ param 0 ^ param 1
 */
@ToString
public class TypeAndParams<T> {
    private final Class<T> root;
    private final Class<?>[] params;

    public TypeAndParams(Class<T> root, Class<?>[] params) {
        this.root = root;
        this.params = params;
    }

    public static <T> TypeAndParams<T> of(@NonNull Class<T> root, @NonNull Class<?> ...params) {
        return new TypeAndParams<>(root, params);
    }

    public Class<T> getRoot() {
        return this.root;
    }

    public Class<?>[] getParams() {
        return this.params;
    }
}
