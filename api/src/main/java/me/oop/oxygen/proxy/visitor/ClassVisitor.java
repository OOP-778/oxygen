package me.oop.oxygen.proxy.visitor;

import lombok.NonNull;
import net.bytebuddy.dynamic.DynamicType;
import me.oop.oxygen.proxy.EntityProxyClassMaker;

public interface ClassVisitor<T> {

    default DynamicType.Builder<T> visitPreBuild(DynamicType.Builder<T> builder, @NonNull EntityProxyClassMaker.BuildMetaData<T> metaData) {
        return builder;
    }

    default void visitPostBuild(@NonNull EntityProxyClassMaker.BuildMetaData<T> metaData) {}

    default boolean accepts(Class<? extends T> clazz) {
        return true;
    }
}
