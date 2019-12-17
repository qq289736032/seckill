package com.jisen.seckill.redislock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Collections;

/**
 * 分布式锁由redis实现
 *  * @author jisen
 * @date 2019/6/15 15:21
 */
@Service
public class RedisLockImpl implements RedisLock {
    /**
     * 分布式锁由redis实现,所以要引入redis
     */
    @Autowired
    private RedisTemplate redisTemplate;

    /*加锁成功*/
    private final String LOCK_SUCCESS = "OK";

    /*如果不存在则添加该值（锁）*/
    private final String SET_IF_NOT_EXIST = "NX";

    /*添加值（锁）和时间*/
    private final String SET_WITH_EXPIRE_TIME = "PX";

    /*释放锁成功*/
    private final Long RELEASE_SUCCESS = 1L;

    /*lua 代码目的是让确认是否同一个requestId和删除key这两个操作的原子性*/
    private static final String RELEASE_LOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";


    /**
     *
     * @param lockKey     锁
     * @param uniqueValue 能够唯一标识请求的值，以此保证锁的加解锁是同一个客户端
     * @param expireTime  过期时间, 单位：milliseconds
     * @return
     */
    @Override
    public boolean lock(String lockKey, String uniqueValue, int expireTime) {

        return (boolean) redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
            Jedis jedis = (Jedis)redisConnection.getNativeConnection();
            String result = jedis.set(lockKey, uniqueValue, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
            if (LOCK_SUCCESS.equals(result)) {
                return true;
            }
            return false;
        });
    }

    /**
     * "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
     *
     * @param lockKey     锁
     * @param uniqueValue 能够唯一标识请求的值，以此保证锁的加解锁是同一个客户端
     * @return
     */
    @Override
    public boolean unlock(String lockKey, String uniqueValue) {
        return (boolean)redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
            Jedis jedis = (Jedis) redisConnection.getNativeConnection();
            Object result = jedis.eval(RELEASE_LOCK_SCRIPT, Collections.singletonList(lockKey),
                    Collections.singletonList(uniqueValue));
            if (RELEASE_SUCCESS.equals(result)) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        });
    }
}
