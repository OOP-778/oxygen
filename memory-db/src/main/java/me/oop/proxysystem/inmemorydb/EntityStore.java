package me.oop.proxysystem.inmemorydb;

import lombok.NonNull;
import me.oop.oxygen.entity.Entity;
import me.oop.oxygen.proxy.ProxyClassWrapper;
import me.oop.oxygen.proxy.visitor.implementation.ControllerGetterVisitor;
import me.oop.oxygen.util.coll.ClassBasedStorage;

public class EntityStore<T extends Entity> extends ClassBasedStorage<T> {
    private final ProxyClassWrapper<T> entityProxyClass;

    public EntityStore(@NonNull Class<T> entityClass) {
        this.entityProxyClass = Entity.proxy(entityClass)
            .visitor(new ControllerGetterVisitor<>())
            .build()
            .make();
    }
}
