package com.jxnu.controller;

import com.jxnu.domain.*;
import com.jxnu.service.CustomerService;
import com.jxnu.service.DishService;
import com.jxnu.service.OrderService;
import com.jxnu.service.TypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @date 2020/4/20 9:46
 */
@Controller
@ResponseBody
@RequestMapping({"/cus", "/mnp"})
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private DishService dishService;
    @Autowired
    private OrderService orderService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private Logger logger = LoggerFactory.getLogger(DishController.class);

    /**
     * 根据shopId获取店铺信息
     *
     * @param shopId
     * @return
     */
    @RequestMapping(path = "/getShopInfoByShopId", params = "shopId")
    public Map<String, Object> getShopInfo(Integer shopId) {
        logger.info("shopId=" + shopId);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.从数据库获取店铺信息
        Shop shop = customerService.getShopInfoByShopId(shopId);
        logger.info("shop=" + shop);

        //2.获取失败
        if (shop == null) {
            map.put("success", false);
            map.put("msg", "获取店铺信息失败");
            return map;
        }

        //3.获取成功
        map.put("success", true);
        map.put("shop", shop);
        return map;
    }


    /**
     * 根据shopId获取菜品分类
     *
     * @param shopId
     * @return
     */
    @RequestMapping(path = "/getTypeByShopId", params = "shopId")
    public Map<String, Object> getTypeByShopId(Integer shopId) {

        logger.info("shopId=" + shopId);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.从数据库获取菜品分类信息
        List<Type> typeList = typeService.findTypeByShopId(shopId);
        logger.info("typeList=" + typeList);

        //权宜之计，mybatis延迟加载导致返回json错误
        List<Type> types1 = new ArrayList<Type>();
        for (Type type : typeList) {
            Type type1 = new Type();
            type1.setTypeId(type.getTypeId());
            type1.setTypeName(type.getTypeName());
            type1.setTypeShopId(type.getTypeShopId());

            types1.add(type1);
        }

        //3.获取成功
        map.put("success", true);
        map.put("typeList", types1);
        return map;
    }

    /**
     * 根据typeId获取菜品信息
     *
     * @param typeId
     * @return
     */
    @RequestMapping(path = "/getDishesByTypeId", params = "typeId")
    public Map<String, Object> getDishesByTypeId(Integer typeId) {
        logger.info("typeId=" + typeId);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.从数据库获取菜品信息
        List<Dish> dishList = dishService.findDishByDishTypeId(typeId);
        logger.info("dishList=" + dishList);

        //2.获取成功
        map.put("success", true);
        map.put("typeList", dishList);
        return map;
    }


    /**
     * 点菜（点的菜还未下订单）
     *
     * @param shopId
     * @param deskId
     * @param headSculpturePath 微信头像路径
     * @param str_dishMap
     * @return
     */
    @RequestMapping(path = "/prePlaceOrder", params = {"shopId", "deskId", "headSculpturePath"})
    public Map<String, Object> prePlaceOrder(Integer shopId, Integer deskId, String headSculpturePath,
                                             @RequestParam Map<String, String> str_dishMap) {
        logger.info("shopId=" + shopId);
        logger.info("deskId=" + deskId);
        logger.info("headSculpturePath=" + headSculpturePath);
        logger.info("str_dishMap=" + str_dishMap);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.将该顾客加入redis缓存点餐人集合中
        stringRedisTemplate.opsForZSet().add("orderPerson" + ":" + shopId + ":" + deskId, headSculpturePath, System.currentTimeMillis());
        stringRedisTemplate.expire("orderPerson" + ":" + shopId + ":" + deskId, 6, TimeUnit.HOURS);

        //2.获取当前点餐人数并过滤掉1分钟内没有发送心跳的顾客（1分钟内没有发送心跳代表该顾客已经没有在点餐了）
        Set<ZSetOperations.TypedTuple<String>> orderPersonOld = stringRedisTemplate.opsForZSet()
                .rangeWithScores("orderPerson" + ":" + shopId + ":" + deskId, 0, -1);
        Set<String> orderPersonNew = new HashSet<>();
        orderPersonOld.stream().filter(s ->
                (System.currentTimeMillis() - s.getScore()) < 60000).forEach(s -> orderPersonNew.add(s.getValue()));

        logger.info("orderPersonOld=" + orderPersonOld);
        logger.info("orderPersonNew=" + orderPersonNew);

        //3.判断该顾客是否是第一次点餐
        if (str_dishMap.size() == 3) {
            map.put("success", true);
            map.put("msg", "加入点菜成功");
            map.put("orderPerson", orderPersonNew);
            map.put("orderPersonSize", orderPersonNew.size());
            return map;
        }

        //4.将dishMap中的key为shopId、deskId、headSculpturePath的键值对去掉（直接用map做参数map就会包含key为shopId、deskId的键值对）
        str_dishMap.remove("shopId");
        str_dishMap.remove("deskId");
        str_dishMap.remove("headSculpturePath");
        logger.info("str_dishMap=" + str_dishMap);

        //5. 将dishMap由Map<String,String>转化Map<Integer,Integer>（如果直接在参数声明为Map<Integer,Integer>在后面使用过程中会有问题）
        Map<Integer, Integer> dishMap = new HashMap<>();
        for (Map.Entry<String, String> entry : str_dishMap.entrySet()) {
            dishMap.put(Integer.parseInt(entry.getKey()), Integer.parseInt(entry.getValue()));
        }
        logger.info("dishMap=" + dishMap);

        //6.将点的菜（还未下单的）加入到redis缓存中
        for (Integer dishId : dishMap.keySet()) {
            stringRedisTemplate.opsForHash().increment("preOrderDish" + ":" + shopId + ":" + deskId, "" + dishId, dishMap.get(dishId));
        }

        //7.返回结果
        map.put("success", true);
        map.put("msg", "点菜成功");
        map.put("orderPerson", orderPersonNew);
        map.put("orderPersonSize", orderPersonNew.size());
        return map;
    }

    /**
     * 下订单（可多次下订单）
     *
     * @param shopId 该订单的所属店铺id
     * @param deskId 该订单的桌号
     *               直接用map做参数map就会包含key为shopId、deskId的键值对，不过会在方法去除掉
     * @return
     */
    @RequestMapping(path = "/placeOrder", params = {"shopId", "deskId"})
    public Map<String, Object> placeOrder(Integer shopId, Integer deskId) {
        logger.info("shopId=" + shopId);
        logger.info("deskId=" + deskId);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.从缓存中获取要下订单的菜品
        Map<Integer, Integer> dishMap = new HashMap<>();
        Map<Object, Object> objDishMap = stringRedisTemplate.opsForHash().entries("preOrderDish" + ":" + shopId + ":" + deskId);
        logger.info("objDishMap=" + objDishMap);
        for (Map.Entry<Object, Object> obj : objDishMap.entrySet()) {
            dishMap.put(Integer.parseInt(obj.getKey().toString()), Integer.parseInt(obj.getValue().toString()));
        }
        logger.info("dishMap=" + dishMap);
        if (dishMap.size() == 0) {
            map.put("success", false);
            map.put("msg", "没有菜品可以下单");
            return map;
        }


        //2.调用service方法
        Integer i = orderService.insertOrder(shopId, deskId, dishMap);

        //3.插入失败
        if (i == 2) {
            map.put("success", false);
            map.put("msg", "要插入的菜品中不都属于此店铺，下订单失败");
            return map;
        }

        //4.清空点了还未下单的菜品缓存
        stringRedisTemplate.delete("preOrderDish" + ":" + shopId + ":" + deskId);

        //5.插入成功
        map.put("success", true);
        map.put("msg", "下订单成功");
        return map;
    }

    /**
     * 获取当前桌的还未结账的订单信息
     *
     * @param shopId
     * @param deskId
     * @return
     */
    @RequestMapping(path = "/getOrder", params = {"shopId", "deskId"})
    public Map<String, Object> getOrder(Integer shopId, Integer deskId) {
        logger.info("shopId=" + shopId);
        logger.info("deskId=" + deskId);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.构建查询条件对象
        Order order = new Order();
        order.setOrderShopId(shopId);
        order.setDeskId(deskId.byteValue());
        order.setOrderStatus((byte) 0);

        //2.从数据库查询
        List<Order> orderList = orderService.getOrder(order);

        //3.失败的情况
        if (orderList.size() > 1) {
            map.put("success", false);
            map.put("msg", "查询到多余1的订单信息，查询失败");
            return map;
        }

        //成功的情况
        if (orderList.size() == 0 || orderList == null) {
            map.put("success", true);
            map.put("order", orderList);
        } else {
            //权宜之计，mybatis延迟加载导致返回json错误
            Order order1 = new Order();
            order1.setOrderId(orderList.get(0).getOrderId());
            order1.setOrderShopId(orderList.get(0).getOrderShopId());
            order1.setDeskId(orderList.get(0).getDeskId());
            order1.setOrderDateBuy(orderList.get(0).getOrderDateBuy());
            order1.setOrderDatePay(orderList.get(0).getOrderDatePay());
            order1.setOrderStatus(orderList.get(0).getOrderStatus());
            order1.setOrderTotalPrice(orderList.get(0).getOrderTotalPrice());
            order1.setItems(new ArrayList<Item>());
            for (Item item : orderList.get(0).getItems()) {
                Item item1 = new Item();
                item1.setItemOrderId(item.getItemOrderId());
                item1.setItemDishId(item.getItemDishId());
                item1.setItemNumber(item.getItemNumber());
                item1.setItemWaitNumber(item.getItemWaitNumber());
                item1.setDish(item.getDish());
                order1.getItems().add(item1);
            }
            map.put("success", true);
            map.put("order", order1);
        }

        //4.从缓存查询已点但还未下单的菜品
        Map<Integer, Integer> dishMap = new HashMap<>();
        Map<Object, Object> objDishMap = stringRedisTemplate.opsForHash().entries("preOrderDish" + ":" + shopId + ":" + deskId);
        logger.info("objDishMap=" + objDishMap);
        for (Map.Entry<Object, Object> obj : objDishMap.entrySet()) {
            dishMap.put(Integer.parseInt(obj.getKey().toString()), Integer.parseInt(obj.getValue().toString()));
        }
        logger.info("dishMap=" + dishMap);

        Map<Object, Integer> dishInfoMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : dishMap.entrySet()) {
            dishInfoMap.put(dishService.findDishByDishId(entry.getKey()), entry.getValue());
        }
        map.put("preOrder", dishInfoMap);

        return map;
    }


    /**
     * 催菜或呼叫服务员
     * @param shopId
     * @param deskId
     * @param callOrUrge 该值为true时代表呼叫服务员，为false时代表催菜，默认为false
     * @return
     */
    @GetMapping(path = "/callAndUrge", params = {"shopId", "deskId","callOrUrge"})
    public Map<String, Object> callWaiterAndUrgeDishes(Integer shopId, Integer deskId,
                                                       @RequestParam(defaultValue = "false") boolean callOrUrge) {
        logger.info("shopId=" + shopId);
        logger.info("deskId=" + deskId);
        logger.info("callOrUrge=" + callOrUrge);

        Map<String, Object> map = new HashMap<String, Object>();

        if (callOrUrge) {
            stringRedisTemplate.opsForZSet().add("callWaiter" + ":" + shopId, String.valueOf(deskId), System.currentTimeMillis());
            stringRedisTemplate.expire("callWaiter" + ":" + shopId, 6, TimeUnit.HOURS);

            map.put("success",true);
            map.put("msg","呼叫服务员成功");
        } else {
            stringRedisTemplate.opsForZSet().add("urgeDishes" + ":" + shopId, String.valueOf(deskId), System.currentTimeMillis());
            stringRedisTemplate.expire("urgeDishes" + ":" + shopId, 6, TimeUnit.HOURS);

            map.put("success",true);
            map.put("msg","催菜成功");
        }

        return map;
    }
}
