package com.mark.framework.cache;

import java.util.HashMap;
import java.util.Iterator;

/**
 * LFU淘汰存储
 *
 * @author mark
 * @date 2017-11-22
 */
public class LFUCache<K extends RequestCacheKey, V> extends AbstractCacheMap<K, V> {

    public LFUCache(int cacheSize) {
        super(cacheSize);
        cacheMap = new HashMap<K, CacheObject<K, V>>(cacheSize);
    }

    /**
     * 实现删除过期对象 和 删除访问次数最少的对象
     */
    @Override
    protected int eliminateCache() {
        Iterator<CacheObject<K, V>> iterator = cacheMap.values().iterator();
        int count = 0;
        long minAccessCount = Long.MAX_VALUE;
        while (iterator.hasNext()) {
            CacheObject<K, V> cacheObject = iterator.next();

            if (cacheObject.isExpired()) {
                iterator.remove();
                count++;
                continue;
            } else {
                minAccessCount = Math.min(cacheObject.accessCount, minAccessCount);
            }
        }

        if (count > 0) {
            return count;
        }
        if (minAccessCount != Long.MAX_VALUE) {
            iterator = cacheMap.values().iterator();
            while (iterator.hasNext()) {
                CacheObject<K, V> cacheObject = iterator.next();

                cacheObject.accessCount -= minAccessCount;

                if (cacheObject.accessCount <= 0) {
                    iterator.remove();
                    count++;
                }
            }
        }

        return count;
    }

}
