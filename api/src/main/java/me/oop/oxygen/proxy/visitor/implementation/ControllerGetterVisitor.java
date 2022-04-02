package me.oop.oxygen.proxy.visitor.implementation;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import lombok.NonNull;
import me.oop.oxygen.entity.Entity;
import me.oop.oxygen.entity.EntityController;
import me.oop.oxygen.proxy.visitor.ClassVisitor;
import me.oop.oxygen.Oxygen;
import me.oop.oxygen.proxy.EntityProxyClassMaker;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * This interceptor intercepts {@link Entity#getController()}
 */
public class ControllerGetterVisitor<T> implements ClassVisitor<T> {

    @Override
    public Builder<T> visitPreBuild(Builder<T> builder, @NonNull EntityProxyClassMaker.BuildMetaData<T> metaData) {
        return builder
            .method(ElementMatchers.isMethod().and(ElementMatchers.returns(EntityController.class)))
            .intercept(MethodDelegation.to(new Interceptor()));
    }

    public static class Interceptor {

        @RuntimeType
        public Object intercept(@Origin Method method,
                                @SuperCall Callable<?> superMethod,
                                @AllArguments Object[] args,
                                @This Object model) throws Exception {
            return Oxygen.getInstance().getEntityController(model);
        }
    }

    @Override
    public boolean accepts(Class<? extends T> clazz) {
        return Entity.class.isAssignableFrom(clazz);
    }
}
