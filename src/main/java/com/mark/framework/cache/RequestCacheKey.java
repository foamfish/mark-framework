package com.mark.framework.cache;

/**
 * Request Cache Key
 *
 * @author mark
 * @date 2017-11-24
 */
public interface RequestCacheKey {
    boolean matchPattern(RequestCacheKey key);
}
