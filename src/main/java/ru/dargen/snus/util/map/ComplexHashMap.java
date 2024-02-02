package ru.dargen.snus.util.map;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Accessors(fluent = true, chain = true)
public class ComplexHashMap<K, V> implements ComplexMap<K, V> {

    private final Map<K, V> asMap = new HashMap<>();
    private final Map<V, K> asReversedMap = new HashMap<>();

    @Override
    public Set<K> keySet() {
        return asMap.keySet();
    }

    @Override
    public Set<V> valuesSet() {
        return asReversedMap.keySet();
    }

    @Override
    public Collection<K> keys() {
        return asReversedMap.values();
    }

    @Override
    public Collection<V> values() {
        return asMap.values();
    }

    @Override
    public void put(K key, V value) {
        asMap.put(key, value);
        asReversedMap.put(value, key);
    }

    @Override
    public V getValue(K key) {
        return asMap.get(key);
    }

    @Override
    public K getKey(V value) {
        return asReversedMap.get(value);
    }

    @Override
    public V removeKey(K key) {
        val value = asMap.remove(key);
        asReversedMap.remove(value);

        return value;
    }

    @Override
    public K removeValue(V value) {
        val key = asReversedMap.remove(value);
        asMap.remove(key);

        return key;
    }

}
