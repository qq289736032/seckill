package com.jisen.seckill.mvcconfig.intercepter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于访问拦截的注解
 * 主要用于防止刷功能的实现
 * @author jisen
 * @date 2019/6/15 12:24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {
    /**
     * 两次请求的最大有效时间间隔，即视两次请求为同一状态的时间间隔
     * @return
     */
    int seconds();

    /**
     * 最大请求次数
     *
     * @return
     */
    int maxAccessCount();

    /**
     * 是否需要重新登录
     *
     * @return
     */
    boolean needLogin() default true;

}
