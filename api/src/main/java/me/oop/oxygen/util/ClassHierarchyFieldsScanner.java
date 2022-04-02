package me.oop.oxygen.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import me.oop.oxygen.annotation.EntityComponent;

public class ClassHierarchyFieldsScanner {

    public static Map<String, Field> scan(@NonNull Class<?> clazz, @NonNull Map<String, Field> scanned,
                                          @NonNull LinkedList<String> path) {
        final Set<Field> fieldsOfHierarchy = ClassUtility.getFieldsOfHierarchy(clazz);
        for (Field field : fieldsOfHierarchy) {
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);

            path.add(field.getName());
            final Class<?> type = field.getType();

            if (type.isAnnotationPresent(EntityComponent.class) || field.isAnnotationPresent(EntityComponent.class)) {
                scanned.put(String.join(".", path), field);
                ClassHierarchyFieldsScanner.scan(type, scanned, path);
            } else {
                scanned.put(String.join(".", path), field);
            }

            path.removeLast();
        }

        return scanned;
    }

    public static Map<String, Field> scan(@NonNull Class<?> clazz) {
        return ClassHierarchyFieldsScanner.scan(clazz, new LinkedHashMap<>(), new LinkedList<>());
    }
}
