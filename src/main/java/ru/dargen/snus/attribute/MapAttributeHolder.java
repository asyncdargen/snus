package ru.dargen.snus.attribute;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("ALL")
public class MapAttributeHolder implements AttributeHolder{

    private final Map<AttributeKey<?>, Attribute<?>> attributes = new ConcurrentHashMap<>();

    @Override
    public <T> boolean hasAttr(AttributeKey<T> key) {
        return attributes.containsKey(key);
    }

    @Override
    public <T> Attribute<T> removeAttr(AttributeKey<T> key) {
        var attr = attributes.remove(key);
        return attr == null ? new Attribute<>(this, key, null) : (Attribute<T>) attr;
    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {
        var attr = attributes.get(key);
        return attr == null ? new Attribute<>(this, key, null) : (Attribute<T>) attr;
    }

    @Override
    public <T> Attribute<T> updateAttr(AttributeKey<T> key, Attribute<T> attr) {
        attributes.put(key, attr);
        return attr;
    }

}
