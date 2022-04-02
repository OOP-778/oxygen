package me.oop.proxysystem.inmemorydb.template;

import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;

public class EntityTemplateCreator<T> {

    // Path to field from root entity to field value
    private final Map<String, Object> template = new HashMap<>();

    private EntityTemplateCreator<T> put(@NonNull String path, @NonNull Object value) {
        this.template.put(path, value);
        return this;
    }
}
