package me.oop.oxygen.entity.component;

import com.oop.memorystore.implementation.index.IndexDefinition;
import java.lang.reflect.Field;
import lombok.ToString;
import me.oop.oxygen.util.coll.ClassBasedStorage;
import me.oop.oxygen.entity.component.EntityFieldComponent.EntityField;

// Path -> Field
// Field -> EntityField
// Class -> All fields
public class EntityFieldComponent extends ClassBasedStorage<EntityField> {

    public static final String OWNING_CLASS_INDEX = "owningClass";
    public static final String FIELD_PATH = "path";

    public EntityFieldComponent() {
        super();
        this.storage.index(OWNING_CLASS_INDEX, IndexDefinition.withKeyMapping((entityField) -> entityField.field.getDeclaringClass()));
        this.storage.index(FIELD_PATH, IndexDefinition.withKeyMapping((entityField) -> entityField.path));
    }

    @ToString
    public static class EntityField {
        private final Field field;
        private final String path;
        private Class<?> declaringClass;

        public EntityField(Field field, String path) {
            this.field = field;
            this.path = path;
        }

        public Field getField() {
            return this.field;
        }

        public String getPath() {
            return this.path;
        }

        public void setDeclaringClass(Class<?> declaringClass) {
            this.declaringClass = declaringClass;
        }

        public Class<?> getDeclaringClass() {
            return this.declaringClass;
        }
    }
}
