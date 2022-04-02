package me.oop.oxygen.entity.component;

import com.oop.memorystore.implementation.index.IndexDefinition;
import me.oop.oxygen.proxy.ProxyClassWrapper;
import me.oop.oxygen.util.coll.ClassBasedStorage;

public class EntityProxiedClasses extends ClassBasedStorage<ProxyClassWrapper> {

    public static final String ORIGINAL_CLASS_INDEX = "originalClass";
    public static final String PROXIED_CLASS_INDEX = "proxiedClass";

    public EntityProxiedClasses() {
        super();
        this.storage.index(ORIGINAL_CLASS_INDEX, IndexDefinition.withKeyMapping(ProxyClassWrapper::getOriginalClass));
        this.storage.index(PROXIED_CLASS_INDEX, IndexDefinition.withKeyMapping(ProxyClassWrapper::getProxiedClass));
    }
}
