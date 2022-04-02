package me.oop.proxysystem.inmemorydb;

import me.oop.oxygen.entity.Entity;

public class TestEntity implements Entity {

    private int number;
    private TestComponent testComponent;

    protected TestEntity() {
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
