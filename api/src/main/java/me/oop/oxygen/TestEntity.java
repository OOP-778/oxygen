package me.oop.oxygen;

import me.oop.oxygen.annotation.LinkToMethod;
import me.oop.oxygen.entity.Entity;

public class TestEntity implements Entity {

    @LinkToMethod(
        setter = "setNumber",
        getter = "getNumber"
    )
    private int number;

    @LinkToMethod(
        setter = "setComponent",
        getter = "getComponent"
    )
    private TestComponent component;

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public TestComponent getComponent() {
        return this.component;
    }

    public void setComponent(TestComponent component) {
        this.component = component;
    }
}
