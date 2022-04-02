package me.oop.oxygen.proxy.visitor.implementation;

import com.oop.memorystore.implementation.query.Query;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.NonNull;
import me.oop.oxygen.Oxygen;
import me.oop.oxygen.annotation.LinkToFieldGetter;
import me.oop.oxygen.annotation.LinkToFieldSetter;
import me.oop.oxygen.annotation.LinkToMethod;
import me.oop.oxygen.entity.EntityController;
import me.oop.oxygen.entity.component.EntityFieldComponent;
import me.oop.oxygen.entity.component.EntityFieldComponent.EntityField;
import me.oop.oxygen.entity.component.EntityMethodComponent;
import me.oop.oxygen.entity.component.EntityMethodComponent.EntityMethod;
import me.oop.oxygen.proxy.ProxyClassWrapper;
import me.oop.oxygen.proxy.visitor.ClassVisitor;
import me.oop.oxygen.storage.EntityStorage;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;
import org.jetbrains.annotations.Nullable;
import me.oop.oxygen.proxy.EntityProxyClassMaker;
import me.oop.oxygen.util.ClassUtility;

public class MethodVisitorUsingLinkTo<T> implements ClassVisitor<T> {
    private final Implementation interceptor = MethodDelegation.to(new Interceptor());

    @Override
    public Builder<T> visitPreBuild(Builder<T> builder, @NonNull EntityProxyClassMaker.BuildMetaData<T> metaData) {
        return builder
            .method(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.namedOneOf("equals", "hashCode", "toString"))))
            .intercept(this.interceptor);
    }

    @Override
    public void visitPostBuild(@NonNull EntityProxyClassMaker.BuildMetaData<T> metaData) {
        final ProxyClassWrapper<T> proxyClass = metaData.getProxyClass();
        final EntityMethodComponent methodComponent = proxyClass.getOrSupply(EntityMethodComponent.class, EntityMethodComponent::new);
        final EntityFieldComponent fieldComponent = proxyClass.get(EntityFieldComponent.class);

        for (EntityField entityField : fieldComponent.getStorage()) {
            final LinkToMethod annotation = entityField.getField().getAnnotation(LinkToMethod.class);
            if (annotation == null) {
                this.findingMethodsWithAnnotations(entityField, methodComponent);
                continue;
            }

            this.findMethod(entityField, annotation.getter(), method -> method.getParameterCount() == 0)
                .ifPresent(method -> {
                    System.out.println("Looking for field: " + entityField.getField().getName());
                    methodComponent.put(new EntityMethod(
                        method,
                        false,
                        entityField
                    ));
                });

            this.findMethod(entityField, annotation.setter(), method -> method.getParameterCount() == 1)
                .ifPresent(method -> {
                    methodComponent.put(new EntityMethod(
                        method,
                        true,
                        entityField
                    ));
                });
        }
    }

    private void findingMethodsWithAnnotations(EntityField entityField, EntityMethodComponent methodComponent) {
        final Stream<Method> methods = ClassUtility.getMethods(
            entityField.getDeclaringClass(),
            method -> method.isAnnotationPresent(LinkToFieldGetter.class) || method.isAnnotationPresent(LinkToFieldSetter.class)
        );

        methods.forEach(method -> {
            final LinkToFieldGetter getterAnnotation = method.getAnnotation(LinkToFieldGetter.class);
            final String name = entityField.getField().getName();
            if (getterAnnotation != null && getterAnnotation.value().contentEquals(name)) {
                methodComponent.put(new EntityMethod(
                    method,
                    false,
                    entityField
                ));
            }

            final LinkToFieldSetter setterAnnotation = method.getAnnotation(LinkToFieldSetter.class);
            if (setterAnnotation != null && setterAnnotation.value().contentEquals(name)) {
                methodComponent.put(new EntityMethod(
                    method,
                    true,
                    entityField
                ));
            }
        });
    }

    private Optional<Method> findMethod(@NonNull EntityField entityField, @Nullable String name, Predicate<Method> predicate) {
        System.out.println(String.format("Looking for method: Name %s Field %s", name, entityField.getField().getName()));
        if (name == null) {
            return Optional.empty();
        }

        System.out.println(entityField.getDeclaringClass());
        return Optional.ofNullable(ClassUtility.getMethod(entityField.getDeclaringClass(), name, predicate));
    }

    public static class Interceptor {

        @RuntimeType
        public Object intercept(@Origin Method method,
                                @SuperCall Callable<?> superMethod,
                                @AllArguments Object[] args,
                                @This Object me) throws Exception {

            final EntityController entityController = Oxygen.getInstance().getEntityController(me);
            final ProxyClassWrapper<?> rootClassWrapper = entityController.get(ProxyClassWrapper.class);

            final EntityMethodComponent methodComponent = rootClassWrapper.get(EntityMethodComponent.class);

            final Optional<EntityMethod> firstMatchingMethod = methodComponent.getStorage().findFirst(Query.where(
                EntityMethodComponent.OWNING_CLASS_INDEX,
                rootClassWrapper.getOriginalClass()
            ).and(
                EntityMethodComponent.METHOD_NAME_INDEX,
                method.getName()
            ));

            if (firstMatchingMethod.isEmpty()) {
                return superMethod.call();
            }

            final EntityStorage storage = entityController.get(EntityStorage.class);
            final EntityMethod entityMethod = firstMatchingMethod.get();

            if (entityMethod.isSetter()) {
                storage.set(entityController, entityMethod.getField().getPath(), args[0]);
                return null;
            }

            return storage.get(entityMethod.getField().getPath());
        }
    }
}
