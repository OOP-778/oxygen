package me.oop.oxygen.proxy.visitor.implementation;

import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import me.oop.oxygen.annotation.EntityComponent;
import me.oop.oxygen.entity.Entity;
import me.oop.oxygen.entity.component.EntityProxiedClasses;
import me.oop.oxygen.proxy.ProxyClassWrapper;
import me.oop.oxygen.proxy.visitor.ClassVisitor;
import me.oop.oxygen.entity.component.EntityFieldComponent;
import me.oop.oxygen.entity.component.EntityFieldComponent.EntityField;
import me.oop.oxygen.proxy.EntityProxyClassMaker;
import me.oop.oxygen.util.ClassHierarchyFieldsScanner;

/**
 * Visit fields and validate their types and create proxied types based of that
 *
 * @param <T>
 */
public class FieldVisitor<T extends Entity> implements ClassVisitor<T> {

    @Override
    public void visitPostBuild(@NonNull EntityProxyClassMaker.BuildMetaData<T> metaData) {
        final ProxyClassWrapper<T> proxyClass = metaData.getProxyClass();

        final EntityFieldComponent fieldComponent = new EntityFieldComponent();
        ClassHierarchyFieldsScanner.scan(proxyClass.getOriginalClass()).forEach((path, field) -> fieldComponent.put(new EntityField(field, path)));

        final Set<Class> proxiedClasses = new HashSet<>();
        final EntityProxiedClasses proxiedClassesComponent = proxyClass.getOrSupply(EntityProxiedClasses.class, EntityProxiedClasses::new);

        for (EntityField entityField : fieldComponent.getStorage()) {
            final Class<?> type = entityField.getField().getType();
            entityField.setDeclaringClass(entityField.getField().getDeclaringClass());

            if (proxiedClasses.contains(type)) {
                continue;
            }

            if (!(type.isAnnotationPresent(EntityComponent.class) || entityField.getField().isAnnotationPresent(EntityComponent.class))) {
                continue;
            }

            final ProxyClassWrapper<?> subProxyClass = Entity.proxy(type).visitor(new ControllerGetterVisitor<>()).build().make();
            entityField.setDeclaringClass(subProxyClass.getProxiedClass());

            proxiedClasses.add(type);
            proxiedClassesComponent.put(Entity.proxy(type).visitor(new ControllerGetterVisitor<>()).build().make());
        }

        fieldComponent.getStorage().reindex();
        metaData.getProxyClass().put(fieldComponent);
    }

}
