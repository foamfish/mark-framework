package com.mark.framework.anotation.requestcache;

import com.mark.framework.cache.Cache;
import com.mark.framework.cache.LFUCache;
import com.mark.framework.cache.LRUCache;
import com.mark.framework.cache.RequestCacheKey;

/**
 *
 * @author mark
 * @date 2017-11-21
 */
public class RequestCacheMemoGeneric<K extends RequestCacheKey, V> implements RequestCacheRepository<K, V> {

    private Cache<K, V> cache;
    public RequestCacheMemoGeneric(Integer cacheSize, RequestCachePolicy policy) {
        switch (policy) {
            case LFU:
                cache = new LFUCache<K, V>(cacheSize);
                break;
            case LRU:
                cache = new LRUCache<K, V>(cacheSize);
                break;
            default:
                break;
        }
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public V put(K key, V o) {
        return cache.put(key, o);
    }

    @Override
    public int remove(K key) {
        return cache.remove(key);
    }

}
