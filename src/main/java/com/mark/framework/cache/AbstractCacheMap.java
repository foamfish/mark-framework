package com.mark.framework.cache;

/**
 * 抽象缺省实现
 *
 * @author mark
 * @date 2017-11-22
 */

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * cache抽象实现
 */
public abstract class AbstractCacheMap<K extends RequestCacheKey, V> implements Cache<K, V> {

    class CacheObject<K2, V2> {
        CacheObject(K2 key, V2 value, long ttl) {
            this.key = key;
            this.cachedObject = value;
            this.ttl = ttl;
            this.lastAccess = System.currentTimeMillis();
        }

        final K2 key;
        final V2 cachedObject;
        long lastAccess;        // 最后访问时间
        long accessCount;       // 访问次数
        long ttl;               // 对象存活时间(time-to-live)

        boolean isExpired() {
            if (ttl == 0) {
                return false;
            }
            return lastAccess + ttl < System.currentTimeMillis();
        }

        V2 getObject() {
            lastAccess = System.currentTimeMillis();
            accessCount++;
            return cachedObject;
        }
    }

    protected Map<K, CacheObject<K, V>> cacheMap;

    private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();
    private final Lock readLock = cacheLock.readLock();
    private final Lock writeLock = cacheLock.writeLock();

    protected int cacheSize; // 缓存大小, 0 -> 无限制

    protected boolean existCustomExpire; //是否设置默认过期时间

    public int getCacheSize() {
        return cacheSize;
    }

    protected long defaultExpire = 0;     // 默认过期时间, 0 -> 永不过期

    public AbstractCacheMap(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public AbstractCacheMap(int cacheSize, long defaultExpire) {
        this.cacheSize = cacheSize;
        this.defaultExpire = defaultExpire;
    }

    public long getDefaultExpire() {
        return defaultExpire;
    }

    protected boolean isNeedClearExpiredObject() {
        return defaultExpire > 0 || existCustomExpire;
    }

    public V put(K key, V value) {
        return put(key, value, defaultExpire);
    }

    public V put(K key, V value, long expire) {
        writeLock.lock();

        try {
            CacheObject<K, V> co = new CacheObject<K, V>(key, value, expire);
            if (expire != 0) {
                existCustomExpire = true;
            }
            if (isFull()) {
                eliminate();
            }
            CacheObject<K, V> old = cacheMap.put(key, co);
            return old == null ? null : old.getObject();
        } finally {
            writeLock.unlock();
        }
    }


    /**
     * {@inheritDoc}
     */
    public V get(K key) {
        readLock.lock();

        try {
            CacheObject<K, V> co = cacheMap.get(key);
            if (co == null) {
                return null;
            }
            if (co.isExpired() == true) {
                cacheMap.remove(key);
                return null;
            }

            return co.getObject();
        } finally {
            readLock.unlock();
        }
    }

    public final int eliminate() {
        writeLock.lock();
        try {
            return eliminateCache();
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 淘汰对象具体实现
     *
     * @return
     */
    protected abstract int eliminateCache();

    public boolean isFull() {
        if (cacheSize == 0) {//o -> 无限制
            return false;
        }
        return cacheMap.size() >= cacheSize;
    }

    public int remove(K key) {
        writeLock.lock();
        try {
            int count = 0;
            Iterator<CacheObject<K, V>> iterator = cacheMap.values().iterator();
            while (iterator.hasNext()) {
                CacheObject<K, V> cacheObject = iterator.next();
                if (cacheObject.key.matchPattern(key)) {
                    iterator.remove();
                    count++;
                }
            }
            return count;
        } finally {
            writeLock.unlock();
        }
    }


    public void clear() {
        writeLock.lock();
        try {
            cacheMap.clear();
        } finally {
            writeLock.unlock();
        }
    }

    public int size() {
        return cacheMap.size();
    }


    public boolean isEmpty() {
        return size() == 0;
    }
}