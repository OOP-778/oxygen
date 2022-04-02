package me.oop.proxysystem.event;

import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.NonNull;

public class EventManager {
    private final Map<Class, Map<Integer, List<EventHandler>>> eventHandlers = new IdentityHashMap<>();

    public <T> Runnable hook(Class<T> eventClass, int priority, @NonNull EventHandler<T> eventHandler) {
        final Map<Integer, List<EventHandler>> priorityMap = this.eventHandlers.computeIfAbsent(eventClass, ($) -> new LinkedHashMap<>());
        final List<EventHandler> eventHandlers = priorityMap.computeIfAbsent(priority, ($) -> new LinkedList<>());

        eventHandlers.add(eventHandler);
        return () -> eventHandlers.remove(eventHandler);
    }

    public void call(@NonNull Object event) {
        final Map<Integer, List<EventHandler>> priorityMap = this.eventHandlers.get(event.getClass());
        if (priorityMap == null) {
            return;
        }

        final List<Entry<Integer, List<EventHandler>>> sortedHandlers = priorityMap.entrySet()
            .stream()
            .sorted(Comparator.comparingInt(Entry::getKey))
            .collect(Collectors.toList());
        Collections.reverse(sortedHandlers);

        for (Entry<Integer, List<EventHandler>> priorityEntry : sortedHandlers) {
            for (EventHandler eventHandler : priorityEntry.getValue()) {
                eventHandler.handle(event);
            }
        }
    }

}
