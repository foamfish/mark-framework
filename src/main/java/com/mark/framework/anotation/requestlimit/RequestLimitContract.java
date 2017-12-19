package com.mark.framework.anotation.requestlimit;

import com.mark.framework.cache.RequestCacheKey;
import com.mark.framework.cache.LRUCache;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

@Aspect
@Component
public class RequestLimitContract {
    private static final Logger logger = LoggerFactory.getLogger(RequestLimitContract.class);

    /**
     * ip#url -> 时间列表
     */
    private static final LRUCache<RequestCacheKey, ConcurrentLinkedQueue<Long>> limitAccessTime =
            new LRUCache<>(1024);

    /**
     * 被封的IP地址
     */
    private static final LRUCache<RequestCacheKey, Long> forbiddenIpAddrs =
            new LRUCache<>(10240);

    /**
     * key是否受限
     * @param key ip#url
     * @param forbiddenPeriod 受限时间
     * @return true受限，false不受限
     */
    private boolean isForbidden(RequestCacheKey key, long forbiddenPeriod) {
        Long forbiddenTime = forbiddenIpAddrs.get(key);
        if (forbiddenTime == null) {
            return false;
        }
        if (System.currentTimeMillis() < forbiddenTime + forbiddenPeriod) {
            return true;
        }
        forbiddenIpAddrs.remove(key);
        return false;
    }

    @Before("@annotation(limit)")
    public void requestLimit(JoinPoint joinPoint, RequestLimit limit) throws Exception {
        DefaultRequestLimitKey key = new DefaultRequestLimitKey(joinPoint);
        if (isForbidden(key, limit.period())) {
            return;
        }
        // 时间
        Long now = System.currentTimeMillis();
        Long before = now - limit.time();

        ConcurrentLinkedQueue<Long> queue = limitAccessTime.get(key);
        int count = 1;
        if (queue != null) {
            Iterator<Long> itr = queue.iterator();
            while (itr.hasNext()) {
                long accessTime = itr.next();
                if (accessTime < before) {
                    itr.remove();
                } else {
                    count++;
                }
            }
        } else {
            queue = new ConcurrentLinkedQueue<>();
        }
        if (count > limit.count()) {
            logger.info(key+ " 超过了次数限制" + limit.count());
            throw new RequestLimitException(key+ " 超过了次数限制" + limit.count());
        }
        queue.add(now);
        limitAccessTime.put(key, queue);
    }
}