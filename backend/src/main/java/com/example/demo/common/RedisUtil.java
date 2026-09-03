package com.example.demo.common;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 * <p>
 * 封装常用的 Redis 操作：设置/获取值、设置过期时间、删除 key 等。
 * 类比前端：高性能版本的 localStorage，自带过期时间。
 * </p>
 */
@Component
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 设置缓存
     *
     * @param key   缓存 key
     * @param value 缓存值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置缓存（带过期时间，单位秒）
     *
     * @param key     缓存 key
     * @param value   缓存值
     * @param timeout 过期时间（秒）
     */
    public void set(String key, Object value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存
     *
     * @param key 缓存 key
     * @return 缓存值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取缓存（指定类型）
     *
     * @param key   缓存 key
     * @param clazz 目标类型
     * @return 缓存值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null && clazz.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    /**
     * 删除缓存
     *
     * @param key 缓存 key
     * @return 是否删除成功
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 判断 key 是否存在
     *
     * @param key 缓存 key
     * @return 是否存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置过期时间
     *
     * @param key     缓存 key
     * @param timeout 过期时间（秒）
     * @return 是否设置成功
     */
    public Boolean expire(String key, long timeout) {
        return redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取过期时间
     *
     * @param key 缓存 key
     * @return 剩余过期时间（秒），-1 表示永不过期，-2 表示 key 不存在
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 自增
     *
     * @param key   缓存 key
     * @param delta 增量
     * @return 增长后的值
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }
}
