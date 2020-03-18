package com.gmall.manager;

import com.gmall.utils.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * test
 *
 * @Author: theliar
 * @CreateTime: 2020-03-07 / 11时 11分 57秒
 * @Description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void test(){
        Jedis jedis = redisUtil.getJedis();

        String ping = jedis.ping();

        System.out.println(ping);
    }




}