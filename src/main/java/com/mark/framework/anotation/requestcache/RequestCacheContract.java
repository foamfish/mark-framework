package com.mark.framework.anotation.requestcache;

import com.mark.framework.cache.RequestCacheKey;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Aspect
@Component
public class RequestCacheContract {
    private static final Logger logger = LoggerFactory.getLogger(RequestCacheContract.class);
    @Value("${mark-framework.request.cache.requestCacheKey:}")
    private String requestCacheKey;

    @Autowired
    private RequestCacheRepository<RequestCacheKey, Object> requestCacheRepository;

    @Around("@annotation(cache)")
    public Object requestCache(ProceedingJoinPoint point, RequestCache cache) throws Throwable {
        RequestCacheKey key = buildKey(point, cache);
        Object o = requestCacheRepository.get(key);
        if (o != null) {
            return o;
        }
        o = point.proceed();
        requestCacheRepository.put(key, o);

        return o;
    }

    /**
     * @return key
     */
    private RequestCacheKey buildKey(ProceedingJoinPoint point, RequestCache cache)
            throws NoSuchMethodException,
            ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (requestCacheKey.length() == 0) {
            return new DefaultRequestCacheKey(point);
        } else {
            return RequestCacheKey.class.cast(
                    Class.forName(requestCacheKey).getConstructor(ProceedingJoinPoint.class)
                            .newInstance(point));
        }
    }
}