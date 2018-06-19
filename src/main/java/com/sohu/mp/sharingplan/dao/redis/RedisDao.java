package com.sohu.mp.sharingplan.dao.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sohu.mp.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public abstract class RedisDao<T> {

    private static final Logger log = LoggerFactory.getLogger(RedisDao.class);

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private ValueOperations<String, String> valOps() {
        return redisTemplate.opsForValue();
    }

    private ZSetOperations<String, String> zSetOperations() {
        return redisTemplate.opsForZSet();
    }

    private ListOperations<String, String> listOperations() {
        return redisTemplate.opsForList();
    }

    private HashOperations<String, String, String> hashOperations() {
        return redisTemplate.opsForHash();
    }

    protected void storeHashField(String key, String field, T obj) {
        try {
            hashOperations().put(key, field, JsonUtil.toJson(obj));
        } catch (Exception e) {
            log.error("error when store values to redis for key: " + key, e);
        }
    }

    protected List<T> multiGetHashFields(String key, Set<String> fieldNames, Class<T> cls) {
        try {
            List<T> result = new ArrayList<>();
            List<String> strList = hashOperations().multiGet(key, fieldNames);
            for (String str : strList) {
                result.add(JsonUtil.fromJson(str, cls));
            }
            return result;
        } catch (Exception e) {
            log.error("error when get values to redis for key: " + key, e);
        }
        return Collections.emptyList();
    }

    protected void deleteHashFiled(String key, String fieldName) {
        try {
            hashOperations().delete(key, fieldName);
        } catch (Exception e) {
            log.error("error when store values to redis for key: " + key, e);
        }
    }

    /**
     * Store the object as String
     *
     * @param key
     * @param obj
     */
    protected void storeValue(String key, T obj) {
        try {
            valOps().set(key, JsonUtil.toJson(obj));
        } catch (Exception e) {
            log.error("error when store values to redis for key: " + key, e);
        }
    }

    protected void incrementKey(String key, int score) {
        try {
            valOps().increment(key, score);
        } catch (Exception e) {
            log.error("error when increment values for key: " + key, e);
        }
    }

    /**** LIST ****/
    protected void storeToList(String key, T obj) {
        try {
            listOperations().rightPush(key, JsonUtil.toJson(obj));
        } catch (JsonProcessingException e) {
            log.error(String.format("error when store list to redis for key: %s, obj: %s", key, obj), e);
        }
    }

    protected void storeList(String key, List<T> list) {
        try {
            for (int i = 0, length = list.size(); i < length; i++) {
                listOperations().rightPush(key, JsonUtil.toJson(list.get(i)));
            }
        } catch (Exception e) {
            log.error("error when store list to redis for key: " + key, e);
        }
    }

    /**** SET ****/
    protected void deleteFromZset(String key, T obj) {
        try {
            zSetOperations().remove(key, JsonUtil.toJson(obj));
        } catch (Exception e) {
            log.error(String.format("error when delete from zset for key: %s, obj: %s", key, obj), e);
        }
    }

    protected void storeZset(String key, T value, double score) {
        try {
            zSetOperations().add(key, JsonUtil.toJson(value), score);
        } catch (Exception e) {
            log.error("error when store zset to redis for key: {}, e: {}", key, e);
        }
    }

    protected void checkZsetSize(String key, int maxSize) {
        try {
            long size = zSetOperations().zCard(key);
            if (size > maxSize) {
                zSetOperations().removeRange(key, maxSize, size - 1);
            }
        } catch (Exception e) {
            log.error("error when store zset for key: {}, e: {}", key, e);
        }
    }

    /**
     * Store the list in sorted set
     *
     * @param key
     * @param list
     */
    protected void storeZset(String key, List<T> list) {
        try {
            for (int i = 0, length = list.size(); i < length; i++) {
                double score = System.currentTimeMillis() + 100.0 * i;
                zSetOperations().add(key, JsonUtil.toJson(list.get(i)), score);
            }
        } catch (Exception e) {
            log.error("error when store zset to redis for key: " + key, e);
        }
    }

    protected void storeZsetMulti(String key, Set<ZSetOperations.TypedTuple<String>> values) {
        try {
            zSetOperations().add(key, values);
        } catch (Exception e) {
            log.error("error when store zset multi to redis for key: " + key, e);
        }
    }

    protected void storeZset(String key, List<T> values, List<Double> scores) {
        try {
            for (int i = 0; i < values.size(); i++) {
                T value = values.get(i);
                Double score = scores.get(i);
                zSetOperations().add(key, JsonUtil.toJson(value), score);
            }
        } catch (Exception e) {
            log.error("error when store zset for key: " + key, e);
        }
    }

    protected void storeZset(String key, List<T> values, List<Double> scores, int maxSize) {
        try {
            for (int i = 0; i < values.size(); i++) {
                T value = values.get(i);
                Double score = scores.get(i);
                zSetOperations().add(key, JsonUtil.toJson(value), score);
            }
            long size = zSetOperations().zCard(key);
            if (size > maxSize) {
                zSetOperations().removeRange(key, maxSize - 1, size - 1);
            }
        } catch (Exception e) {
            log.error("error when store zset for key: " + key, e);
        }
    }

    /**
     * increase key
     *
     * @param key
     */
    protected Long increase(String key) {
        try {
            return redisTemplate.opsForValue().increment(key, 1);
        } catch (Exception e) {
            log.error("error when increase redis key: " + key, e);
        }
        return null;
    }

    /**
     * Get object from String value
     *
     * @param key
     * @return
     */
    protected T queryFromValue(String key, Class<T> cls) {
        try {
            String result = valOps().get(key);
            if (result == null) {
                return null;
            }
            return JsonUtil.fromJson(result, cls);
        } catch (Exception e) {
            log.error("error when query value from redis for key: " + key, e);
            return null;
        }
    }

    /**
     * get list of object from list
     *
     * @param key
     * @param cls
     * @return
     */
    protected List<T> queryFromList(String key, Class<T> cls) {
        return queryFromList(key, 0, -1, cls);
    }

    /**
     * get list of object from list with start and end
     *
     * @param key
     * @param cls
     * @return
     */
    protected List<T> queryFromList(String key, int start, int end, Class<T> cls) {
        try {
            List<String> objects = listOperations().range(key, start, end);
            if (objects == null) {
                return Collections.emptyList();
            }
            List<T> results = new ArrayList<>();
            for (String object : objects) {
                results.add(JsonUtil.fromJson(object, cls));
            }
            return results;
        } catch (Exception e) {
            log.error("error when query list from redis for key: " + key, e);
            return Collections.emptyList();
        }
    }

    /**
     * Get list of objects from sorted set
     *
     * @param key
     * @return
     */
    protected List<T> queryFromZset(String key, Class<T> cls) {
        return queryFromZset(key, 0, -1, cls);
    }

    /**
     * Get list of objects from sorted set with start and end
     *
     * @param key
     * @return
     */
    protected List<T> queryFromZset(String key, int start, int end, Class<T> cls) {
        try {
            Set<String> objects = zSetOperations().range(key, start, end);
            if (objects == null) {
                return Collections.emptyList();
            }
            List<T> results = new ArrayList<>();
            for (String object : objects) {
                results.add(JsonUtil.fromJson(object, cls));
            }
            return results;
        } catch (Exception e) {
            log.error("error when query zset from redis for key: " + key, e);
            return Collections.emptyList();
        }
    }

    /**
     * whether the key exists
     *
     * @param key
     * @return
     */
    protected boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("error when check exists from redis for key: " + key, e);
            return false;
        }

    }

    /**
     * Delete the key from redis.
     *
     * @param key
     */
    protected void deleteKey(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error(String.format("error when delete key: %s from redis", key), e);
        }
    }

    /**
     * Delete the key from redis.
     *
     * @param keys
     */
    protected void deleteKeys(List<String> keys) {
        try {
            redisTemplate.delete(keys);
        } catch (Exception e) {
            log.error("error when delete keys from redis", e);
        }
    }

    /**
     * set the key an expire timeout.
     *
     * @param key
     * @param timeout
     * @param timeUnit valid TimeUnit
     */
    protected void expireKey(String key, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplate.expire(key, timeout, timeUnit);
        } catch (Exception e) {
            log.error(String.format("error when expire key: %s from redis", key), e);
        }
    }

    /**
     * Set the key an expire timeout.
     *
     * @param key
     * @param timeout
     */
    protected void expireKey(String key, long timeout) {
        try {
            redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(String.format("error when expire key: %s from redis", key), e);
        }
    }
}
