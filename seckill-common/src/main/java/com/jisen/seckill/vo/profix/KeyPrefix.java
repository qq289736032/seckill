package com.jisen.seckill.vo.profix;

/**
 * redis键的前缀，
 * 之所以在key前面设置一个前缀时因为避免出现设置相同的key的情况，通过前缀加以区分
 * @author jisen
 * @date 2019/6/14 20:24
 */
public interface KeyPrefix {
    /**
     * key的过期时间
     *
     * @return 过期时间
     */
    int expireSeconds();

    /**
     * key的前缀
     *
     * @return 前缀
     */
    String getPrefix();
}
