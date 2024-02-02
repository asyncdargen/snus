package ru.dargen.snus.attribute;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record AttributeKey<T>(String key) {

    private static final Map<String, AttributeKey<?>> ATTRIBUTE_POOL = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> AttributeKey<T> get(String key) {
        return (AttributeKey<T>) ATTRIBUTE_POOL.getOrDefault(key, new AttributeKey<T>(key));
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

}
