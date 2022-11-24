package com.example.sync_everything.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Set;

/**
 * @author ForeverDdB
 * @ClassName Redis
 * @Description redis工具类
 * @createTime 2022年 09月20日 21:16
 **/
@Component
public class Redis {
    private static JedisPool jedisPool;

    @Autowired
    public void init(JedisPool jedisPool) {
        Redis.jedisPool = jedisPool;
    }

    public static String set(String key,String value){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String set(String key,String value, long expire_time){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setex(key, expire_time, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long sAdd(String setName, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sadd(setName, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long sDel(String setName, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.srem(setName, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long sLen(String setName) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.scard(setName);
        } catch (Exception e) {
            return null;
        }
    }

    public static Boolean sIsMember(String setName, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sismember(setName, value);
        } catch (Exception e) {
            return null;
        }
    }

    public static Set<String> sMembers(String setName) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.smembers(setName);
        } catch (Exception e) {
            return null;
        }
    }

    public static Long zAdd(String setName, String value, double score) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zadd(setName, score, value);
        } catch (Exception e) {
            return null;
        }
    }



    public static Double zScore(String setName, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zscore(setName, value);
        } catch (Exception e) {
            return null;
        }
    }

    public static Long zLen(String setName) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zcard(setName);
        } catch (Exception e) {
            return null;
        }
    }

    public static String lIndex(String listName, Long index) {
        try (Jedis jedis = jedisPool.getResource()){
            return jedis.lindex(listName, index);
        } catch (Exception e) {
            return null;
        }
    }

    public static Set<String> zRange(String setName, long start, long stop) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrange(setName, start, stop);
        } catch (Exception e) {
            return null;
        }
    }

    public static Set<String> zRangeByScore(String setName, double min, double max) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScore(setName, min, max);
        } catch (Exception e) {
            return null;
        }
    }

    public static Long zRem(String setName, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrem(setName, value);
        } catch (Exception e) {
            return null;
        }
    }

    public static Long lPush(String listName, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpush(listName, value);
        } catch (Exception e) {
            return null;
        }
    }

    public static String lPop(String listName) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpop(listName);
        } catch (Exception e) {
            return null;
        }
    }

    public static String rPop(String listName) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rpop(listName);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<String> lRange(String listName, int start, int stop) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(listName, start, stop);
        } catch (Exception e) {
            return null;
        }
    }

    public static Long lLen(String listName) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.llen(listName);
        } catch (Exception e) {
            return null;
        }
    }

    public static Boolean exists(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        } catch (Exception e) {
            return null;
        }
    }

    public static Long del(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long ttl(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.ttl(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
