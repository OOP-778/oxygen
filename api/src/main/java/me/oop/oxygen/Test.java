package me.oop.oxygen;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import me.oop.oxygen.entity.Entity;
import me.oop.oxygen.proxy.visitor.implementation.ControllerGetterVisitor;
import me.oop.oxygen.proxy.visitor.implementation.NativeInterceptorVisitor;
import me.oop.oxygen.util.TypeAndParams;
import me.oop.oxygen.entity.EntityFactory;
import me.oop.oxygen.proxy.visitor.implementation.FieldVisitor;
import me.oop.oxygen.proxy.visitor.implementation.MethodVisitorUsingLinkTo;

public class Test {

    public static void main(String[] args) {
        new Oxygen();

        final EntityFactory factory = new EntityFactory();

        factory.register(Map.class, (builder) -> builder
            .registering(
                (typeAndParams) -> Map.class.isAssignableFrom(typeAndParams.getRoot()),
                (typeAndParams) -> Entity.proxy(typeAndParams.getRoot())
                    .visitor(new ControllerGetterVisitor<>())
                    .visitor(new NativeInterceptorVisitor<>("put", "remove"))
                    .build()
                    .make()
            ));

        factory.register(Collection.class, (builder) -> builder
            .registering(
                (typeAndParams) -> Collection.class.isAssignableFrom(typeAndParams.getRoot()),
                (typeAndParams) -> Entity.proxy(typeAndParams.getRoot())
                    .visitor(new ControllerGetterVisitor<>())
                    .visitor(new NativeInterceptorVisitor<>("add", "remove"))
                    .build()
                    .make()
            ));

        factory.register(TestEntity.class, (builder) -> builder
            .registering(
                (typeAndParams) -> typeAndParams.getRoot() == TestEntity.class,
                (typeAndParams) -> Entity.proxy(typeAndParams.getRoot())
                    .visitor(new ControllerGetterVisitor())
                    .visitor(new FieldVisitor())
                    .visitor(new MethodVisitorUsingLinkTo())
                    .build()
                    .make()
            )
        );

        final TestEntity instance = factory.createInstance(TypeAndParams.of(TestEntity.class), null);
        System.out.println(instance);
    }
}
