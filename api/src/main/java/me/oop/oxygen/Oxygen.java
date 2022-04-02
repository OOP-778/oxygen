package me.oop.oxygen;

import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Objects;
import lombok.NonNull;
import me.oop.oxygen.entity.EntityController;

public class Oxygen {
    private final Map<Object, EntityController> knwonEntityReferences = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

    private static Oxygen INSTANCE;

    Oxygen() {
        INSTANCE = this;
    }

    public static Oxygen getInstance() {
        return INSTANCE;
    }

    public void dispose(@NonNull Object entity) {
        this.knwonEntityReferences.remove(entity);
    }

    public void register(@NonNull Object entity, @NonNull EntityController controller) {
        this.knwonEntityReferences.put(entity, controller);
    }

    public EntityController getEntityController(@NonNull Object entity) {
        return Objects.requireNonNull(this.knwonEntityReferences.get(entity), String.format("Failed to find reference of %s in memory", entity));
    }
}
