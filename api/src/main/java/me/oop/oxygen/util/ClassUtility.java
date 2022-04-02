package me.oop.oxygen.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;

public class ClassUtility {

    public static List<Class<?>> getSupersAndItself(@NonNull Class<?> clazz) {
        Class<?> current = clazz;
        final Set<Class<?>> supers = new HashSet<>();

        while (current != null && (current != Object.class)) {
            supers.add(current);
            supers.addAll(Arrays.asList(clazz.getInterfaces()));

            current = current.getSuperclass();
        }

        return new LinkedList<>(supers);
    }

    @SafeVarargs
    public static Set<Field> getFieldsOfHierarchy(@NonNull Class<?> clazz, Predicate<Field>... filters) {
        return Stream.concat(Arrays.stream(clazz.getFields()), Arrays.stream(clazz.getDeclaredFields()))
            .filter((field) -> Arrays.stream(filters).allMatch((predicate) -> predicate.test(field)))
            .collect(Collectors.toSet());
    }

    @SafeVarargs
    public static Method getMethod(@NonNull Class<?> clazz, String name, Predicate<Method>... filters) {
        final Predicate<Method>[] newFilters = new Predicate[1];
        newFilters[0] = (method) -> method.getName().contentEquals(name);

        getMethods(clazz).forEach((method -> System.out.println(method.getName())));

        final Stream<Method> methods = getMethods(
            clazz,
            Stream.concat(Arrays.stream(newFilters), Arrays.stream(filters)).toArray(Predicate[]::new)
        );

        return methods.peek((method) -> System.out.println(method.getName())).findFirst().orElseThrow(() -> new IllegalStateException(
            String.format("Failed to find method in %s in class %s", name, clazz.getSimpleName())
        ));
    }

    @SafeVarargs
    public static Stream<Method> getMethods(@NonNull Class<?> clazz, Predicate<Method>... filters) {
        final Set<Method> methods = new HashSet<>();
        methods.addAll(List.of(clazz.getDeclaredMethods()));
        methods.addAll(List.of(clazz.getMethods()));

        return methods.stream()
            .filter(method -> Arrays.stream(filters).allMatch(filter -> filter.test(method)))
            .map(method -> {
                try {
                    method.setAccessible(true);
                    return method;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    return null;
                }
            });
    }
}
