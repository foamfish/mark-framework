package com.mark.framework.anotation.requestcache;

import com.mark.framework.cache.RequestCacheKey;
import com.mark.framework.exception.base.MyBaseException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Request Cache Key
 *
 * @author mark
 * @date 2017-11-24
 */
public class DefaultRequestCacheKey implements RequestCacheKey, Serializable {

    private String clazzKey;
    private String methodKey;
    private String paramKey = "";
    private String valueKey = "";

    public static Builder newBuilder() {
        return Builder.create();
    }

    /**
     * 构建器
     */
    public static final class Builder<T> {
        private Class<T> clazz;
        private String methodName;
        private List<Class> paramsTypes;
        private List<Object> args;

        public Builder setClazz(Class<T> clazz) {
            this.clazz = clazz;
            return this;
        }

        public Builder setMethodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public Builder setParamsTypes(List<Class> paramsTypes) {
            this.paramsTypes = paramsTypes;
            return this;
        }

        public Builder setArgs(List<Object> args) {
            this.args = args;
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
        public DefaultRequestCacheKey build(Builder builder) {
            return new DefaultRequestCacheKey(builder);
        }
    }

    private <T> DefaultRequestCacheKey(Builder<T> builder) {
        if (builder.clazz == null || StringUtils.isEmpty(builder.methodName)) {
            throw new MyBaseException("类名或者方法名非法！");
        }

        // class类名
        this.clazzKey = builder.clazz.getName();
        // 方法名
        methodKey = builder.methodName;

        StringBuilder builderParamKey = new StringBuilder();
        if (builder.paramsTypes != null) {
            for (int i = 0; i < builder.paramsTypes.size(); i++) {
                if (i != 0) {
                    builderParamKey.append("#");
                }
                builderParamKey.append(builder.paramsTypes.get(i).getName());
            }
        }
        paramKey = builderParamKey.toString();

        StringBuilder builderValueKey = new StringBuilder();
        if (builder.args != null) {
            for (int i = 0; i < builder.args.size(); i++) {
                if (i != 0) {
                    builderValueKey.append("#");
                }
                builderValueKey.append(builder.args.get(i));
            }
        }
        valueKey = builderValueKey.toString();
    }

    private DefaultRequestCacheKey() {
    }

    public DefaultRequestCacheKey(ProceedingJoinPoint point) throws NoSuchMethodException {
        Object[] args = point.getArgs();

        Signature signature = point.getSignature();
        if (!(signature instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        MethodSignature methodSignature = (MethodSignature) signature;
        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());

        clazzKey = currentMethod.getDeclaringClass().getName();
        methodKey = currentMethod.getName();
        Class[] parameterTypes = methodSignature.getParameterTypes();
        StringBuilder builderParamKey = new StringBuilder();
        StringBuilder builderValueKey = new StringBuilder();
        if (parameterTypes != null) {
            for (int i = 0; i < parameterTypes.length; i++) {
                if (i != 0) {
                    builderParamKey.append("#");
                    builderValueKey.append("#");
                }
                builderParamKey.append(parameterTypes[i].getName());
                builderValueKey.append(args[i]);
            }
        }
        paramKey = builderParamKey.toString();
        valueKey = builderValueKey.toString();
    }

    protected String key() {
        return clazzKey + methodKey + paramKey + valueKey;
    }

    @Override
    public boolean matchPattern(RequestCacheKey key) {
        DefaultRequestCacheKey defaultRequestCacheKey = (DefaultRequestCacheKey)key;
        if (!clazzKey.equals(defaultRequestCacheKey.clazzKey)) {
            return false;
        }
        if (!methodKey.equals(defaultRequestCacheKey.methodKey)) {
            return false;
        }
        if (!paramKey.equals(defaultRequestCacheKey.paramKey)) {
            return false;
        }
        if (valueKey == null) {
            return true;
        }
        return valueKey.equals(defaultRequestCacheKey.valueKey);
    }

    @Override
    public String toString() {
        return "DefaultRequestCacheKey{" +
                "clazzKey='" + clazzKey + '\'' +
                ", methodKey='" + methodKey + '\'' +
                ", paramKey='" + paramKey + '\'' +
                ", valueKey='" + valueKey + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultRequestCacheKey that = (DefaultRequestCacheKey) o;

        if (clazzKey != null ? !clazzKey.equals(that.clazzKey) : that.clazzKey != null) return false;
        if (methodKey != null ? !methodKey.equals(that.methodKey) : that.methodKey != null) return false;
        if (paramKey != null ? !paramKey.equals(that.paramKey) : that.paramKey != null) return false;
        return valueKey != null ? valueKey.equals(that.valueKey) : that.valueKey == null;
    }

    @Override
    public int hashCode() {
        int result = clazzKey != null ? clazzKey.hashCode() : 0;
        result = 31 * result + (methodKey != null ? methodKey.hashCode() : 0);
        result = 31 * result + (paramKey != null ? paramKey.hashCode() : 0);
        result = 31 * result + (valueKey != null ? valueKey.hashCode() : 0);
        return result;
    }
}
