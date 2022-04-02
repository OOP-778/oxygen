package me.oop.oxygen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.Nullable;

/**
 * This annotation is used to link field to method or otherwise method to field
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface LinkToMethod {
    @Nullable
    String setter();

    @Nullable
    String getter();
}
