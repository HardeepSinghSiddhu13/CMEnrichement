package com.samsung.nmt.cmenrichment.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * This class provide generic implementation of
 * {@link com.samsung.nmt.cmenrichment.cache} using map.
 *
 */
@Component
@Scope(scopeName = "prototype")
public class GenericCacheImpl<K, V> implements Cache<K, V> {

    Map<K, V> map = null;

    public GenericCacheImpl() {
        map = new ConcurrentHashMap<>();
    }

    @Override
    public void add(K k, V v) {
        map.putIfAbsent(k, v);
    }

    @Override
    public V get(K k) {
        return map.get(k);
    }

    @Override
    public String toString() {
        return map.toString();
    }

}
