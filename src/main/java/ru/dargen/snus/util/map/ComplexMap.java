package ru.dargen.snus.util.map;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ComplexMap<K, V> {

    Map<K, V> asMap();

    Map<V, K> asReversedMap();

    Set<K> keySet();

    Set<V> valuesSet();

    Collection<K> keys();

    Collection<V> values();

    void put(K key, V value);

    V getValue(K key);

    K getKey(V value);

    V removeKey(K key);

    K removeValue(V value);

}
