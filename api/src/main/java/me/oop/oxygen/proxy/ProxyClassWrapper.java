package me.oop.oxygen.proxy;

import java.lang.reflect.Constructor;
import lombok.SneakyThrows;
import lombok.ToString;
import me.oop.oxygen.Oxygen;
import me.oop.oxygen.entity.EntityController;
import me.oop.oxygen.storage.EntityStorage;
import me.oop.oxygen.util.coll.ClassBasedStorage;
import org.jetbrains.annotations.Nullable;

@ToString
public class ProxyClassWrapper<T> extends ClassBasedStorage<Object> {

    protected Class<? extends T> proxiedClass;
    protected Class<T> originalClass;
    protected Constructor<T> constructor;

    public ProxyClassWrapper(Class<T> originalClass) {
        this.originalClass = originalClass;
    }

    public T newInstance() {
        return this.newInstance(null);
    }

    @SneakyThrows
    public T newInstance(@Nullable EntityController controller) {
        if (controller == null) {
            controller = this.createController();
        }

        final T proxiedObject = this.constructor.newInstance();
        Oxygen.getInstance().register(proxiedObject, controller);

        return proxiedObject;
    }

    protected EntityController createController() {
        final EntityController entityController = new EntityController();
        entityController.put(this);
        entityController.put(new EntityStorage());

        return entityController;
    }

    public Class<? extends T> getProxiedClass() {
        return this.proxiedClass;
    }

    public Class<T> getOriginalClass() {
        return this.originalClass;
    }
}
