package me.oop.oxygen.entity;

import lombok.NonNull;
import me.oop.oxygen.Oxygen;
import me.oop.oxygen.proxy.EntityProxyClassMaker;
import me.oop.oxygen.proxy.EntityProxyClassMaker.EntityProxyClassMakerBuilder;
import me.oop.oxygen.proxy.visitor.implementation.ControllerGetterVisitor;

public interface Entity {

    /**
     *
     * This method will be intercepted by {@link ControllerGetterVisitor}
     */
    @NonNull
    default EntityController getController() {
        return null;
    }

    static <T> EntityProxyClassMakerBuilder<T> proxy(@NonNull Class<T> clazz) {
        return EntityProxyClassMaker.builder(clazz);
    }

    /**
     * Dispose the entity from memory
     */
    default void dispose() {
        Oxygen.getInstance().dispose(this);
    }
}
