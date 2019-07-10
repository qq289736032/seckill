package com.jisen.seckillcommon.vo.profix;

/**
 * @author jisen
 * @date 2019/6/14 20:15
 */
public abstract class BaseKeyPrefix implements KeyPrefix {
    /**
     * 过期时间
     */
    int expireSeconds;

    /**
     * 前缀
     */
    String prefix;

    /**
     * 因为过期时间受到redis的缓存策略影响，这里设置一个默认过期时间为0，即不过期，
     * @param prefix 前缀
     */
    public BaseKeyPrefix(String prefix) {
        this(0, prefix);
    }

    //构造函数
    public BaseKeyPrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String simpleName = this.getClass().getSimpleName();
        return simpleName + ":" + prefix;
    }
}
