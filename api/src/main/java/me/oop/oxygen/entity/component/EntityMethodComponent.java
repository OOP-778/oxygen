package me.oop.oxygen.entity.component;

import com.oop.memorystore.implementation.index.IndexDefinition;
import com.oop.memorystore.implementation.index.comparison.string.CaseInsensitiveComparisonPolicy;
import java.lang.reflect.Method;
import lombok.ToString;
import me.oop.oxygen.entity.component.EntityFieldComponent.EntityField;
import me.oop.oxygen.entity.component.EntityMethodComponent.EntityMethod;
import me.oop.oxygen.util.coll.ClassBasedStorage;

public class EntityMethodComponent extends ClassBasedStorage<EntityMethod> {

    public static final String OWNING_CLASS_INDEX = "owningClass";
    public static final String METHOD_NAME_INDEX = "methodName";

    public EntityMethodComponent() {
        this.createNameIndex();
        this.createDeclaringClassIndex();
    }

    private void createDeclaringClassIndex() {
        this.storage.index(
            OWNING_CLASS_INDEX,
            IndexDefinition.withKeyMapping(EntityMethod::getDeclaringClass)
        );
    }

    private void createNameIndex() {
        final IndexDefinition<String, EntityMethod> nameIndex = IndexDefinition
            .withKeyMapping((method) -> method.getMethod().getName());
        nameIndex.withComparisonPolicy(new CaseInsensitiveComparisonPolicy());

        this.storage.index(
            METHOD_NAME_INDEX,
            nameIndex
        );
    }

    @ToString
    public static class EntityMethod {
        private final Method method;
        private final EntityField field;
        private final boolean setter;
        private final Class<?> declaringClass;

        public EntityMethod(Method method, boolean setter, EntityField field) {
            this.field = field;
            this.method = method;
            this.setter = setter;
            this.declaringClass = method.getDeclaringClass();
        }

        public Method getMethod() {
            return this.method;
        }

        public boolean isSetter() {
            return this.setter;
        }

        public Class<?> getDeclaringClass() {
            return this.declaringClass;
        }

        public EntityField getField() {
            return this.field;
        }
    }
}
