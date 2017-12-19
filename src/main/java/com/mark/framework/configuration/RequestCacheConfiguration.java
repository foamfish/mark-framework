package com.mark.framework.configuration;


import com.mark.framework.anotation.requestcache.RequestCachePolicy;
import com.mark.framework.anotation.requestcache.RequestCacheRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

/**
 * RequestCache缓存配置
 *
 * @author mark
 * @date 2017-11-21
 */
@Component
@Configuration
@ConditionalOnProperty(prefix = "mark-framework.request.cache", name="enable")
public class RequestCacheConfiguration {

    @Value("${mark-framework.request.cache.policy:LFU}")
    private RequestCachePolicy policy;

    @Value("${mark-framework.request.cache.repository:com.mark.framework.anotation.requestcache.RequestCacheMemoGeneric}")
    private String repository;

    @Value("${mark-framework.request.cache.size:1024}")
    private Integer size;


    public RequestCachePolicy getPolicy() {
        return policy;
    }

    public String getRepository() {
        return repository;
    }

    public Integer getSize() {
        return size;
    }

    /**
     * 缺省缓存大小
     */
    private int DEFAULT_SIZE = 1024;

    /**
     * RequestCache 主要的管理器
     *
     * @return
     */
    @Bean
    public RequestCacheRepository requestCacheManager()
            throws ClassNotFoundException,
            IllegalAccessException,
            InstantiationException,
            NoSuchMethodException,
            InvocationTargetException {
        return RequestCacheRepository.class.cast(
                Class.forName(repository).getConstructor(Integer.class, RequestCachePolicy.class)
                        .newInstance(size == null ? DEFAULT_SIZE : size,
                                policy == null? RequestCachePolicy.LRU : policy));
    }
}
