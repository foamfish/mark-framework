/*
 * Copyright (C)  2017 - 2018 Microscene Inc., All Rights Reserved.
 *
 * @author: mark@vb.com.cn
 * @Date: 2017.9.24
 */

package com.mark.framework.anotation.requestcache;

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
public @interface RequestCache {
}