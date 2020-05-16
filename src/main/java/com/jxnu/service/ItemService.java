package com.jxnu.service;

/**
 * @date 2020/5/16 20:29
 * @author dk
 * 关于订单明细的service
 */
public interface ItemService {

    /**
     * 使菜品待上数量加一或减一
     * @param shopId 要修改菜品待上数量的订单明细所属店铺的id
     * @param deskId 要修改菜品待上数量的订单明细所属桌位的id
     * @param dishId 要修改菜品待上数量的菜品id
     * @param number 该值为+1或-1，代表着菜品待上加一或减一
     * @return true 修改成功，false 修改失败
     */
    boolean updateItemWaitNumber(Integer shopId,Integer deskId,Integer dishId,Integer number);
}
