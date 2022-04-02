package me.oop.oxygen.event;

import me.oop.oxygen.entity.EntityController;

public class ValueChangeEvent {
    private final Object model;
    private final Object previousValue;

    private final EntityController entityController;
    private final String path;

    private Object newValue;

    public ValueChangeEvent(Object model, Object previousValue, EntityController entityController, String path, Object newValue) {
        this.model = model;
        this.previousValue = previousValue;
        this.entityController = entityController;
        this.path = path;
        this.newValue = newValue;
    }

    public Object getModel() {
        return this.model;
    }

    public Object getPreviousValue() {
        return this.previousValue;
    }

    public Object getNewValue() {
        return this.newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }
}
