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
    * 修改一个订单明细(修改数量)
    */
   Integer updateItemNumber(@Param("itemNumber") Integer itemNumber,@Param("itemOrderId") Integer itemOrderId);
}
