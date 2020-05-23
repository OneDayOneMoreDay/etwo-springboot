package com.jxnu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxnu.domain.Shop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @date 2020/5/23 21:10
 * <p>
 * 存放一些店铺端、服务员端、顾客端都要访问的接口
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private Logger logger = LoggerFactory.getLogger(CommonController.class);

    /**
     * 获取一分钟内有哪些桌位催菜和呼叫服务员
     * <p>
     * 店铺端和服务员端要定时访问此接口（建议1-3秒访问一次）
     *
     * @param request
     * @return 返回的map的urgeDishes属性代表的是催菜桌号列表，callWaiter属性代表的是呼叫服务员桌号列表
     */
    @GetMapping("/getCallAndUrge")
    public Map<String, Object> getCallWaiterAndUrgeDishes(HttpServletRequest request) throws JsonProcessingException {

        HttpSession session = request.getSession();
        logger.info("getCallWaiterAndUrgeDishesSession = " + session);

        Map<String, Object> map = new HashMap<String, Object>();

        //1、获取session，现在店铺和服务员端登录使用的是相同的邮箱、密码，它们登录成功后都在session存入一个名为shop的属性
        Shop shop = (Shop) session.getAttribute("shop");
        logger.info("shopFromSession=" + shop);

        //2.从缓存中获取一分钟内有哪些桌位呼叫服务员
        //2.1从缓存中获取所有的呼叫服务员的桌号
        Set<ZSetOperations.TypedTuple<String>> callWaiterOld = stringRedisTemplate.opsForZSet()
                .rangeWithScores("callWaiter" + ":" + shop.getShopId(), 0, -1);
        logger.info("callWaiterOld=" + new ObjectMapper().writeValueAsString(callWaiterOld));
        //2.2剔除超过一分钟前的呼叫服务员的桌号，并把剔除后的桌号赋值给新的set集合
        Set<String> callWaiterNew = new HashSet<>();
        Set<ZSetOperations.TypedTuple<String>> callWaiterOldNew = new HashSet<>();
        for (ZSetOperations.TypedTuple<String> s : callWaiterOld) {
            if (System.currentTimeMillis() - s.getScore() < 60000) {
                callWaiterNew.add(s.getValue());
                callWaiterOldNew.add(s);
            }
        }
        logger.info("callWaiterOldNew=" + new ObjectMapper().writeValueAsString(callWaiterOldNew));
        logger.info("callWaiterNew=" + callWaiterNew);

        //2.3把结果剔除后的Set<ZSetOperations.TypedTuple<String>>重新放入缓存
        if (callWaiterOld.size()==callWaiterOldNew.size()){

        } else if (callWaiterOldNew.isEmpty()) {
            stringRedisTemplate.delete("callWaiter" + ":" + shop.getShopId());
        } else {
            stringRedisTemplate.delete("callWaiter" + ":" + shop.getShopId());
            stringRedisTemplate.opsForZSet().add("callWaiter" + ":" + shop.getShopId(), callWaiterOldNew);
        }

        //3.从缓存中获取一分钟内有哪些桌位催菜
        //3.1从缓存中获取所有的催菜的桌号
        Set<ZSetOperations.TypedTuple<String>> urgeDishesOld = stringRedisTemplate.opsForZSet()
                .rangeWithScores("urgeDishes" + ":" + shop.getShopId(), 0, -1);
        logger.info("urgeDishesOld=" + new ObjectMapper().writeValueAsString(urgeDishesOld));
        //3.2剔除超过一分钟前的催菜的桌号，并把剔除后的桌号赋值给新的set集合
        Set<String> urgeDishesNew = new HashSet<>();
        Set<ZSetOperations.TypedTuple<String>> urgeDishesOldNew = new HashSet<>();
        for (ZSetOperations.TypedTuple<String> s : urgeDishesOld) {
            if (System.currentTimeMillis() - s.getScore() < 60000) {
                urgeDishesNew.add(s.getValue());
                urgeDishesOldNew.add(s);
            }
        }
        logger.info("urgeDishesOldNew=" + new ObjectMapper().writeValueAsString(urgeDishesOldNew));
        logger.info("urgeDishesNew=" + urgeDishesNew);
        //3.3把结果剔除后的Set<ZSetOperations.TypedTuple<String>>重新放入缓存
        if (urgeDishesOldNew == urgeDishesOld){

        } else if (urgeDishesOldNew.isEmpty()) {
            stringRedisTemplate.delete("urgeDishes" + ":" + shop.getShopId());
        } else {
            stringRedisTemplate.delete("urgeDishes" + ":" + shop.getShopId());
            stringRedisTemplate.opsForZSet().add("urgeDishes" + ":" + shop.getShopId(), urgeDishesOldNew);
        }

        //4.返回结果
        map.put("success", true);
        map.put("urgeDishes", urgeDishesNew);
        map.put("callWaiter", callWaiterNew);

        return map;
    }
}
