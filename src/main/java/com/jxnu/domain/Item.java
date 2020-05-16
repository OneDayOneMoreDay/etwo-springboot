package com.jxnu.domain;

import lombok.Data;

/**
 * @author dk
 * @version 1.0
 * @date 2020/3/17 9:02
 * 订单明细
 */
@Data
public class Item {

    //订单编号
    private Integer itemOrderId;
    //菜品编号
    private Integer itemDishId;
    //菜品品数量
    private Integer itemNumber;
    //该菜品待上的数量
    private Integer itemWaitNumber;

    //订单明细所有的菜品
    private Dish dish;
}
