package com.jxnu.mapper;

import com.jxnu.domain.Order;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author dk
 * @version 1.0
 * @date 2020/3/26 13:24
 */
@Repository
public interface OrderMapper {

    /**
     * 根据条件查询订单，延迟加载
     * @return
     */
    List<Order> findOrderByCondition(Order order);

    /**
     * 插入一个订单
     * @param order
     * @return 1 插入成功；2 要插入的菜品中不都属于此店铺，插入失败
     */
    Integer insertOrder(Order order);

    /**
     * 修改一个订单（修改付款时间（将时间从null修改到具体值）和订单状态）
     * @param order
     * @return
     */
    Integer updateOrder(Order order);

    /**
     * 修改一个订单（修改订单总价）
     * @param orderTotalPrice 订单总加
     * @param orderId 订单id
     * @return
     */
    Integer updateOrderTotalPrice(@Param("orderTotalPrice") Float orderTotalPrice,@Param("orderId") Integer orderId);

    /**
     * 获取某店铺所有还存在未结账订单的桌号
     * @param shopId
     * @return
     */
    List<Integer> getOrderDishDesks(Integer shopId);
}
