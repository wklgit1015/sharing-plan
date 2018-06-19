package com.sohu.mp.sharingplan.dao.redis.impl;

import com.sohu.mp.sharingplan.dao.redis.RedisDao;
import org.springframework.stereotype.Repository;

@Repository
public class RedisLockDao extends RedisDao {

    private static final String KEY_FORMAT = "lock_%s_%s";
    private static final long EXPIRES = 5;

    public boolean lock(String keyPrefix, String keySuffix) {
        String key = getKey(keyPrefix, keySuffix);
        long value = increase(key);
        if (value == 1) {
            expireKey(key, EXPIRES);
        }
        return value == 1;
    }

    public void unLock(String keyPrefix, String keySuffix) {
        deleteKey(getKey(keyPrefix, keySuffix));
    }

    private String getKey(String keyPrefix, String keySuffix) {
        return String.format(KEY_FORMAT, keyPrefix, keySuffix);
    }

}
