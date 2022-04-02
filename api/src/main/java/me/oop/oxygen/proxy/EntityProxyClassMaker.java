package me.oop.oxygen.proxy;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.oop.oxygen.entity.Entity;
import me.oop.oxygen.entity.component.EntityProxiedClasses;
import me.oop.oxygen.proxy.visitor.ClassVisitor;
import me.oop.oxygen.util.coll.ClassBasedStorage;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;

public class EntityProxyClassMaker<T> {
    protected final Class<T> originalClass;
    protected final ProxyClassWrapper<T> proxyClass;
    protected final List<ClassVisitor<T>> visitors;
    protected DynamicType.Builder<T> builder;
    protected BuildMetaData<T> buildMetaData;

    protected EntityProxyClassMaker(@NonNull Class<T> clazz) {
        this.originalClass = clazz;
        this.proxyClass = new ProxyClassWrapper<>(this.originalClass);
        this.builder = new ByteBuddy()
            .subclass(this.originalClass)
            .implement(Entity.class);
        this.visitors = new LinkedList<>();
        this.buildMetaData = new BuildMetaData<>(this.proxyClass);
    }

    public static <T> EntityProxyClassMakerBuilder<T> builder(Class<T> clazz) {
        return new EntityProxyClassMakerBuilder<>(clazz);
    }

    @SneakyThrows
    public ProxyClassWrapper<T> make() {
        for (ClassVisitor<T> visitor : this.visitors) {
            this.builder = visitor.visitPreBuild(this.builder, this.buildMetaData);
        }

        this.proxyClass.proxiedClass = this.builder.make()
            .load(this.getClass().getClassLoader())
            .getLoaded();

        final EntityProxiedClasses proxiedClasses = this.proxyClass.getOrSupply(EntityProxiedClasses.class, EntityProxiedClasses::new);
        proxiedClasses.put(this.proxyClass);

        for (ClassVisitor<T> visitor : this.visitors) {
            visitor.visitPostBuild(this.buildMetaData);
        }

        this.findConstructor();
        return this.proxyClass;
    }

    private void findConstructor() {
        for (Constructor<?> constructor : this.proxyClass.proxiedClass.getDeclaredConstructors()) {
            if (constructor.getParameterCount() != 0) {
                continue;
            }

            this.proxyClass.constructor = (Constructor<T>) constructor;
            break;
        }

        if (this.proxyClass.constructor == null) {
            throw new IllegalStateException(String.format(
                "Unable to find empty (protected|public) constructor for %s",
                this.proxyClass.originalClass.getSimpleName()
            ));
        }

        this.proxyClass.constructor.setAccessible(true);
    }

    public static class BuildMetaData<T> extends ClassBasedStorage<Object> {
        private final ProxyClassWrapper<T> proxyClass;

        public BuildMetaData(ProxyClassWrapper<T> proxyClass) {
            super();
            this.proxyClass = proxyClass;
        }

        public ProxyClassWrapper<T> getProxyClass() {
            return this.proxyClass;
        }
    }

    public static class EntityProxyClassMakerBuilder<T> {
        private final Class<T> originalClass;
        private final List<ClassVisitor<T>> visitors;

        public EntityProxyClassMakerBuilder(Class<T> clazz) {
            this.originalClass = clazz;
            this.visitors = new LinkedList<>();
        }

        public EntityProxyClassMakerBuilder<T> visitor(@NonNull ClassVisitor<T>... visitors) {
            this.visitors.addAll(Arrays.asList(visitors));
            return this;
        }

        public EntityProxyClassMaker<T> build() {
            final EntityProxyClassMaker<T> maker = new EntityProxyClassMaker<>(this.originalClass);
            maker.visitors.addAll(this.visitors);

            return maker;
        }
    }
}
