package com.jxnu.controller;

import com.jxnu.domain.Item;
import com.jxnu.domain.Order;
import com.jxnu.domain.Shop;
import com.jxnu.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @date 2020/4/20 21:18
 *
 * 跟订单有关的controller
 */
@Controller
@ResponseBody
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    private Logger logger = LoggerFactory.getLogger(DishController.class);

    /**
     * 得到一家店铺所有未支付的订单
     * @param session
     * @return
     */
    @RequestMapping(path = "/getNoPay")
    public Map<String, Object> getOrderNoPay(HttpSession session){

        Map<String, Object> map = new HashMap<String, Object>();

        //1.从session中获取已经登录的shop对象
        Shop shopFromSession = (Shop) session.getAttribute("shop");
        logger.info("shopFromSession = " + shopFromSession);

        //2.获取数据从数据库
        //2.1 构造order做为查询参数
        Order order = new Order();
        order.setOrderShopId(shopFromSession.getShopId());
        order.setOrderStatus((byte)0);

        //2.2 查数据库
        List<Order> orderList = orderService.getOrder(order);

        //权宜之计，mybatis延迟加载导致返回json错误
        List<Order> orderList1 = new ArrayList<>();
        for (Order order1 : orderList) {
            Order order2 = new Order();
            order2.setItems(new ArrayList<Item>());
            for (Item item : order1.getItems()) {
                Item item1 = new Item();
                item1.setItemOrderId(item.getItemOrderId());
                item1.setItemDishId(item.getItemDishId());
                item1.setItemNumber(item.getItemNumber());
                item1.setDish(item.getDish());
                order2.getItems().add(item1);
            }
            order2.setOrderId(order1.getOrderId());
            order2.setOrderShopId(order1.getOrderShopId());
            order2.setDeskId(order1.getDeskId());
            order2.setOrderDateBuy(order1.getOrderDateBuy());
            order2.setOrderDatePay(order1.getOrderDatePay());
            order2.setOrderStatus(order1.getOrderStatus());
            order2.setOrderTotalPrice(order1.getOrderTotalPrice());
            orderList1.add(order2);
        }

        //3 返回结果
        map.put("success", true);
        map.put("orderList", orderList1);
        return map;
    }

    /**
     * 得到一家店铺所有已支付的订单
     * @param session
     * @return
     */
    @RequestMapping(path = "/getHavePay")
    public Map<String, Object> getOrderHavePay(HttpSession session){

        Map<String, Object> map = new HashMap<String, Object>();

        //1.从session中获取已经登录的shop对象
        Shop shopFromSession = (Shop) session.getAttribute("shop");
        logger.info("shopFromSession = " + shopFromSession);

        //2.获取数据从数据库
        //2.1 构造order做为查询参数
        Order order = new Order();
        order.setOrderShopId(shopFromSession.getShopId());
        order.setOrderStatus((byte)1);

        //2.2 查数据库
        List<Order> orderList = orderService.getOrder(order);

        //权宜之计，mybatis延迟加载导致返回json错误
        List<Order> orderList1 = new ArrayList<>();
        for (Order order1 : orderList) {
            Order order2 = new Order();
            order2.setItems(new ArrayList<Item>());
            for (Item item : order1.getItems()) {
                Item item1 = new Item();
                item1.setItemOrderId(item.getItemOrderId());
                item1.setItemDishId(item.getItemDishId());
                item1.setItemNumber(item.getItemNumber());
                item1.setDish(item.getDish());
                order2.getItems().add(item1);
            }
            order2.setOrderId(order1.getOrderId());
            order2.setOrderShopId(order1.getOrderShopId());
            order2.setDeskId(order1.getDeskId());
            order2.setOrderDateBuy(order1.getOrderDateBuy());
            order2.setOrderDatePay(order1.getOrderDatePay());
            order2.setOrderStatus(order1.getOrderStatus());
            order2.setOrderTotalPrice(order1.getOrderTotalPrice());
            orderList1.add(order2);
        }

        //3 返回结果
        map.put("success", true);
        map.put("orderList", orderList1);
        return map;
    }

    /**
     * 结账
     * 即订单的状态从未支付变成已支付 0->1
     * @param session
     * @return
     */
    @RequestMapping(path = "/pay",params = "orderId")
    public Map<String, Object> pay(HttpSession session,Integer orderId){
        logger.info("orderId = " + orderId);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.从session中获取已经登录的shop对象
        Shop shopFromSession = (Shop) session.getAttribute("shop");
        logger.info("shopFromSession = " + shopFromSession);

        //2 向数据库修改order
        Integer i = orderService.updateOrder(orderId, shopFromSession.getShopId());

        //3.失败的两种情况
        if (i==2){
            map.put("success", false);
            map.put("msg", "此orderId不属于此店铺，结账失败");
            return map;
        }
        if (i!=2 && i!=1){
            map.put("success", false);
            map.put("msg", "结账失败");
            return map;
        }

        //4 成功，返回结果
        map.put("success", true);
        map.put("msg", "结账成功");
        return map;
    }
}
