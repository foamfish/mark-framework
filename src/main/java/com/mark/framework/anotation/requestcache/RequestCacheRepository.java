package com.mark.framework.anotation.requestcache;

import com.mark.framework.cache.RequestCacheKey;

/**
 * 缓存仓库
 *
 * @author mark
 * @date 2017-11-21
 */
public interface RequestCacheRepository<K extends RequestCacheKey, V> {
    V get(K key);
    V put(K key, V v);
    int remove(K key);
}
