package me.oop.proxysystem.event;

@FunctionalInterface
public interface EventHandler<T> {

    void handle(T event);

}
