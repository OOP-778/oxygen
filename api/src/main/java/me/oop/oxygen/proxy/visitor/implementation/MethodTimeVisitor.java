package me.oop.oxygen.proxy.visitor.implementation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.NonNull;
import me.oop.oxygen.proxy.visitor.ClassVisitor;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;
import me.oop.oxygen.proxy.EntityProxyClassMaker;

public class MethodTimeVisitor<T> implements ClassVisitor<T> {
    private final List<String> methodList;
    private final Implementation interceptor;

    public MethodTimeVisitor(String... methods) {
        this.methodList = Arrays.asList(methods);
        this.interceptor = MethodDelegation.to(new Interceptor());
    }

    @Override
    public Builder<T> visitPreBuild(Builder<T> builder, @NonNull EntityProxyClassMaker.BuildMetaData<T> metaData) {
        for (String methodName : this.methodList) {
            builder = builder.method(ElementMatchers.isMethod().and(ElementMatchers.named(methodName)))
                .intercept(this.interceptor);
        }

        return builder;
    }

    public static class Interceptor {

        @RuntimeType
        public Object intercept(@Origin Method method,
                                @SuperCall Callable<?> superMethod,
                                @AllArguments Object[] args,
                                @This Object me) throws Exception {
            final long before = System.currentTimeMillis();
            final Object call = superMethod.call();
            final long after = System.currentTimeMillis();

            System.out.println(String.format(
                "Operation (%s:%s) took %s ms",
                me.getClass().getSimpleName().split("\\$")[0],
                method.getName(),
                (after - before)
            ));

            return call;
        }
    }

}
