package com.jxnu.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author dk
 * @version 1.0
 * @date 2020/3/17 9:02
 */
@Data
public class Order {

    //订单编号
    private Integer orderId;
    //店铺编号
    private Integer orderShopId;
    //桌位编号
    private Byte deskId;
    //订单状态（0待付款，1已付款）
    private Byte orderStatus;
    //下单时间
    private Date orderDateBuy;
    //结账时间
    private Date orderDatePay;
    //总金额
    private Float orderTotalPrice;

    //订单所有的订单明细
    private List<Item> items;
}
