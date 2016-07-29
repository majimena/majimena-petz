package org.majimena.petical.batch.scraping.utils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by todoken on 2016/05/15.
 */
public class NullableHashMap<K, V> extends ConcurrentHashMap<K, V> {
    @Override
    public V put(K key, V value) {
        if (value != null) {
            return super.put(key, value);
        }
        return value;
    }
}
