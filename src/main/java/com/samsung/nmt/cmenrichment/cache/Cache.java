package com.samsung.nmt.cmenrichment.cache;

/**
 * Define generic interface for storing cache values. Implementation of this
 * class will simply provide in memory key-value store with no eviction policy.
 *
 * @param <K>
 *            key
 * @param <V>
 *            value
 */
public interface Cache<K, V> {
    /**
     * Add key-value in cache.
     *
     * @param key
     *            key to be used to store value
     * @param value
     *            value to be stored
     */
    void add(K key, V value);

    /**
     * Get value based on passed key.
     *
     * @param key
     *            key used to fetch value
     * @return value
     */
    V get(K key);
}
