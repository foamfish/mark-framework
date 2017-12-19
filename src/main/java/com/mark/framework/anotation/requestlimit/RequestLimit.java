/*
 * Copyright (C)  2017 - 2018 Microscene Inc., All Rights Reserved.
 *
 * @author: mark@vb.com.cn
 * @Date: 2017.9.24
 */

package com.mark.framework.anotation.requestlimit;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mark
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface RequestLimit {
    /**
     *
     * 允许访问的次数，默认值300
     */
    int count() default 300;

    /**
     *
     * 时间段，单位为毫秒，默认值一分钟
     */
    long time() default 60000;

    /**
     *
     * 封6个小时
     */
    long period() default 6*60*60*1000;

}