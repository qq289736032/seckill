package com.jisen.seckill.vo.profix;

import java.io.Serializable;

/**
 * @author jisen
 * @date 2019/6/15 13:12
 */
public class AccessKeyPrefix extends BaseKeyPrefix implements Serializable {
    public AccessKeyPrefix(String prefix) {
        super(prefix);
    }

    public AccessKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    /**
     * 设置过期时间
     */
    public static AccessKeyPrefix withExpire(int expireSeconds) {
        return new AccessKeyPrefix(expireSeconds, "access");
    }

}
