package com.mark.framework.anotation.requestlimit;

import com.mark.framework.cache.RequestCacheKey;
import com.mark.framework.exception.base.MyBaseException;
import com.mark.framework.util.HttpRequestUtil;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;

/**
 * Request Limit Key
 *
 * @author mark
 * @date 2017-11-24
 */
public class DefaultRequestLimitKey implements RequestCacheKey, Serializable {

    private static Logger logger = LoggerFactory.getLogger(DefaultRequestLimitKey.class);

    private String ip;
    private String url;

    public static Builder newBuilder() {
        return Builder.create();
    }

    /**
     * 构建器
     */
    public static final class Builder<T> {
        private String ip;
        private String url;

        public Builder setIp(String ip) {
            this.ip = ip;
            return this;
        }
        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        private Builder() {
        }
        private static Builder create() {
            return new Builder();
        }

        /**
         * Bean构建器
         * @param builder
         */
        public DefaultRequestLimitKey build(Builder builder) {
            return new DefaultRequestLimitKey(builder);
        }
    }

    private <T> DefaultRequestLimitKey(Builder<T> builder) {
        if (StringUtils.isEmpty(builder.ip)) {
            throw new MyBaseException("ip不能为空！");
        }
        this.ip = builder.ip;
        this.url = builder.url;
    }

    private DefaultRequestLimitKey() {
    }

    public DefaultRequestLimitKey(JoinPoint point) throws NoSuchMethodException, IOException {
        Object[] args = point.getArgs();
        HttpServletRequest request = null;
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest) {
                request = (HttpServletRequest) arg;
                break;
            }
        }
        if (request == null) {
            throw new RequestLimitException("方法中缺失HttpServletRequest参数");
        }

        // key
        ip = HttpRequestUtil.getIpAddress(request);
        url = request.getRequestURL().toString();
        logger.info("用户IP[" + ip + "]访问地址[" + url);
    }

    @Override
    public boolean matchPattern(RequestCacheKey key) {
        DefaultRequestLimitKey defaultRequestLimitKey = DefaultRequestLimitKey.class.cast(key);
        return (defaultRequestLimitKey.ip.equals(ip));
    }

    @Override
    public String toString() {
        return "DefaultRequestLimitKey{" +
                "ip='" + ip + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultRequestLimitKey that = (DefaultRequestLimitKey) o;

        if (ip != null ? !ip.equals(that.ip) : that.ip != null) return false;
        return url != null ? url.equals(that.url) : that.url == null;
    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}
