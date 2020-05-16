package com.jxnu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxnu.domain.Dish;
import com.jxnu.mapper.DishMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EtwoSpringbootApplicationTests {

    @Autowired
    DataSource dataSource;

    @Autowired
    DishMapper dishMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void contextLoads() throws SQLException {
//        System.out.println(dataSource);
        System.out.println(dataSource.getConnection());
    }

    @Test
    public void test1() throws SQLException {
        List<Dish> dishByDishTypeId = dishMapper.findDishByDishTypeId(1000);
        System.out.println(dishByDishTypeId);
    }

    @Test
    public void test2() throws SQLException {
        Long l1 = stringRedisTemplate.opsForSet().add("test2", "123");
        Long l2 = stringRedisTemplate.opsForSet().add("test2", "123");
        Long l3 = stringRedisTemplate.opsForSet().add("test2", "1234");
        System.out.println(l1);
        System.out.println(l2);
        System.out.println(l3);
        Set<String> test2 = stringRedisTemplate.opsForSet().members("test2");
        System.out.println(test2);
    }

    @Test
    public void test3() throws SQLException {
        Long l1 = stringRedisTemplate.opsForSet().add("test3", "123");
        stringRedisTemplate.expire("test3", 10, TimeUnit.SECONDS);
    }

    @Test
    public void test4() throws SQLException {
        // 开启事务支持，在同一个 Connection 中执行命令
        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.multi();
        Long l1 = stringRedisTemplate.opsForSet().add("test4", "123");
        stringRedisTemplate.expire("test4",30,TimeUnit.SECONDS);
        stringRedisTemplate.exec();

    }
    @Test
    public void test5() throws SQLException, InterruptedException, JsonProcessingException {
        stringRedisTemplate.opsForZSet().add("test5", "1",System.currentTimeMillis());
        Thread.sleep(100);
        stringRedisTemplate.opsForZSet().add("test5", "2",System.currentTimeMillis());
        Thread.sleep(100);
        stringRedisTemplate.opsForZSet().add("test5", "3",System.currentTimeMillis());
        Thread.sleep(100);
        stringRedisTemplate.opsForZSet().add("test5", "1",System.currentTimeMillis());
        Set<ZSetOperations.TypedTuple<String>> test5 = stringRedisTemplate.opsForZSet().rangeWithScores("test5", 0, -1);
        for (ZSetOperations.TypedTuple<String> stringTypedTuple : test5) {
            System.out.println(stringTypedTuple.getValue());
            System.out.println(System.currentTimeMillis()-stringTypedTuple.getScore());
        }
        System.out.println(new ObjectMapper().writeValueAsString(test5));
        Set<String> set = new HashSet<>();
        test5.stream().filter(s->(System.currentTimeMillis()-s.getScore())<500).forEach(s->set.add(s.getValue()));
        System.out.println(set);
    }

    @Test
    public void test6() throws SQLException, InterruptedException, JsonProcessingException {
        stringRedisTemplate.opsForHash().increment("test6","1",1);
        stringRedisTemplate.opsForHash().increment("test6","2",1);
        stringRedisTemplate.opsForHash().increment("test6","1",1);
    }

    @Test
    public void test7() throws SQLException, InterruptedException, JsonProcessingException {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries("preOrderDish:1000:66");
        System.out.println(entries);
    }
}
