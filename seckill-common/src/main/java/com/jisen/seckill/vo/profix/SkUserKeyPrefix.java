package com.jisen.seckill.vo.profix;

import java.io.Serializable;

/**
 * @author jisen
 * @date 2019/6/14 20:15
 */
public class SkUserKeyPrefix extends BaseKeyPrefix implements Serializable {
    public static final int TOKEN_EXPIRE = 10*60;// 缓存有效时间为30min

    public SkUserKeyPrefix(String prefix) {
        super(prefix);
    }

    public SkUserKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    /**
     * 用户cookie，token设置过期时间
     */
    public static SkUserKeyPrefix TOKEN = new SkUserKeyPrefix(TOKEN_EXPIRE, "token");

    public static SkUserKeyPrefix token = new SkUserKeyPrefix(TOKEN_EXPIRE, "token");

    /**
     * 用于存储用户对象到redis的key前缀,永不过期
     */
    public static SkUserKeyPrefix getSeckillUserById = new SkUserKeyPrefix(0, "id");
    public static SkUserKeyPrefix SK_USER_PHONE = new SkUserKeyPrefix(0, "id");
}
