package com.jxnu.service;


import com.jxnu.domain.Order;

import java.util.List;
import java.util.Map;

/**
 * @date 2020/4/20 19:09
 */
public interface OrderService {

    /**
     * 插入一条订单
     * 此方法配置了事务
     * @param shopId 该订单的所属店铺
     * @param deskId 该订单的桌号
     * @param dishMap key是菜品id，value是该菜品在此订单中的数量
     * @return 1 插入成功；2 该桌还存在未结账订单,不能插入新的订单，插入失败;3 要插入的菜品中不都属于此店铺，插入失败
     */
    Integer insertOrder(Integer shopId, Integer deskId, Map<Integer,Integer> dishMap);

    /**
     * 根据order获取order信息
     * @param order
     * @return
     */
    List<Order> getOrder(Order order);

    /**
     * 结账（将orderStatus从0->1）
     * @param orderId
     * @return
     */
    Integer updateOrder(Integer orderId,Integer shopId);

    /**
     * 获取某店铺所有还存在未结账订单的桌号
     * @param shopId
     * @return
     */
    List<Integer> getOrderDishDesks(Integer shopId);
}
