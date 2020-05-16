package com.jxnu.mapper;

import com.jxnu.domain.Item;
import com.jxnu.domain.Order;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author dk
 * @version 1.0
 * @date 2020/3/26 13:28
 */
@Repository
public interface ItemMapper {

   /**
    * 根据订单id查询订单明细
    * @return
    */
   List<Item> findItemByItemOrderId(Integer itemOrderId);

   /**
    * 插入一个订单明细
    * @param item
    * @return
    */
   Integer insertItem(Item item);

   /**
    * 修改一个订单明细(修改已点的该菜品数量和该菜品待上的数量)
    * @param itemOrderId 要修改的订单明细所属的订单的订单id
    * @param itemIncrNumber 该订单明细新增的菜品数量
    * @param itemIncrWaitNumber 该订单明细新增的待上菜品数量
    * @return
    */
   Integer updateItem(@Param("itemOrderId") Integer itemOrderId,
                      @Param("itemDishId") Integer itemDishId,
                            @Param("itemIncrNumber") Integer itemIncrNumber,
                            @Param("itemIncrWaitNumber") Integer itemIncrWaitNumber);

   /**
    * 使菜品待上数量加一或减一
    * @param shopId 要修改菜品待上数量的订单明细所属店铺的id
    * @param deskId 要修改菜品待上数量的订单明细所属桌位的id
    * @param dishId 要修改菜品待上数量的菜品id
    * @param number 该值为+1或-1，代表着菜品待上加一或减一
    * @return
    */
   Integer updateItemWaitNumber(@Param("shopId") Integer shopId,
                              @Param("deskId") Integer deskId,
                              @Param("dishId") Integer dishId,
                              @Param("number") Integer number);
}
