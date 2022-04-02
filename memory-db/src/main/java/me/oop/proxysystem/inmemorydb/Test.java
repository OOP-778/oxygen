package me.oop.proxysystem.inmemorydb;

import me.oop.oxygen.entity.component.EntityProxiedClasses;
import me.oop.oxygen.proxy.visitor.implementation.FieldVisitor;
import me.oop.oxygen.entity.Entity;
import me.oop.oxygen.proxy.ProxyClassWrapper;
import me.oop.oxygen.proxy.visitor.implementation.ControllerGetterVisitor;

public class Test {

    public static void main(String[] args) {
        final ProxyClassWrapper<TestEntity> build = Entity.proxy(TestEntity.class)
            .visitor(new ControllerGetterVisitor<>())
            .visitor(new FieldVisitor<>())
            .build()
            .make();

        final TestEntity testEntity = build.newInstance();
        final ProxyClassWrapper<?> proxyClass = testEntity.getController().get(ProxyClassWrapper.class);
        final EntityProxiedClasses entityProxiedClasses = proxyClass.get(EntityProxiedClasses.class);

        for (ProxyClassWrapper aClass : entityProxiedClasses.getStorage()) {
            System.out.println(aClass);
        }

    }
}
