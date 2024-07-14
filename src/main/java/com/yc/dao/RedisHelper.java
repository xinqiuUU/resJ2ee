package com.yc.dao;


import redis.clients.jedis.Jedis;

/*
      Redis连接对象
 */
public class RedisHelper {
    public static Jedis getRedisInstance(){
        Jedis jedis = new Jedis(  DbProperties.getInstance().getProperty("redis.host"),
                Integer.parseInt(DbProperties.getInstance().getProperty("redis.port")));
        return jedis;
    }
}
