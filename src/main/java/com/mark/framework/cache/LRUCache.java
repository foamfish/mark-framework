/*
 * Copyright (C)  2017 - 2018 Microscene Inc., All Rights Reserved.
 *
 * @author: mark@vb.com.cn
 * @Date: 2017.9.21
 */
package com.mark.framework.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU淘汰存储
 *
 * @param <K>
 * @param <V>
 */
public class LRUCache<K extends RequestCacheKey, V> extends AbstractCacheMap<K, V> {
    private static final long serialVersionUID = -1882071901467368406L;

    public LRUCache(int cacheSize) {
        super(cacheSize);
        this.cacheMap = new LinkedHashMap<K, CacheObject<K, V>>(cacheSize, 1f, true) {
            @Override
            protected boolean removeEldestEntry(
                    Map.Entry<K, CacheObject<K, V>> eldest) {
                return LRUCache.this.removeEldestEntry(eldest);
            }
        };
    }

    private boolean removeEldestEntry(Map.Entry<K, CacheObject<K, V>> eldest) {
        return cacheSize == 0 ? false:size() > cacheSize;
    }

    /**
     * 只需要实现清除过期对象就可以了,linkedHashMap已经实现LRU
     */
    @Override
    protected int eliminateCache() {
        if (!isNeedClearExpiredObject()) {
            return 0;
        }
        Iterator<CacheObject<K, V>> iterator = cacheMap.values().iterator();
        int count = 0;
        while (iterator.hasNext()) {
            CacheObject<K, V> cacheObject = iterator.next();
            if (cacheObject.isExpired()) {
                iterator.remove();
                count++;
            }
        }
        return count;
    }
}

