package me.oop.oxygen.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import me.oop.oxygen.entity.EntityController;

public class EntityStorage {
    private final Map<String, Object> storage;

    public EntityStorage(Map<String, Object> map) {
        this.storage = map;
    }

    public EntityStorage() {
        this(new ConcurrentHashMap<>());
    }

    public void set(@NonNull EntityController controller, @NonNull String path, Object value) {
        this.storage.put(path,value);
    }

    public Object get(@NonNull String path) {
       return this.get(path);
    }
}
