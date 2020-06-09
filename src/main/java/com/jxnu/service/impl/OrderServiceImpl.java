package com.jxnu.service.impl;

import com.jxnu.domain.Dish;
import com.jxnu.domain.Item;
import com.jxnu.domain.Order;
import com.jxnu.domain.Type;
import com.jxnu.mapper.DishMapper;
import com.jxnu.mapper.ItemMapper;
import com.jxnu.mapper.OrderMapper;
import com.jxnu.service.OrderService;
import com.jxnu.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @date 2020/4/20 19:16
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private TypeService typeService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 插入一条订单
     * 此方法配置了事务
     * @param shopId 该订单的所属店铺
     * @param deskId 该订单的桌号
     * @param dishMap key是菜品id，value是该菜品在此订单中的数量
     * @return 1 插入成功；2 要插入的菜品中不都属于此店铺，插入失败
     */
    @Transactional
    @Override
    public Integer insertOrder(Integer shopId, Integer deskId, Map<Integer, Integer> dishMap) {

        //1 判断要插入的菜品是否都属于此店铺以及菜品剩余数量是否都大于顾客点的数量
        //1.1 获取此店铺的所有的菜品分类以及菜品
        List<Type> typeList = typeService.findTypeByShopId(shopId);
        //1.2 获取该订单中的菜品id构成的集合
        Set<Integer> dishIdSet = dishMap.keySet();
        int count = dishIdSet.size();
        float totalPrice=0;//该订单所有菜品的价格
        //1.3 遍历typeList来判断要插入的菜品是否都属于此店铺以及菜品剩余数量是否都大于顾客点的数量
        for (Type type : typeList) {
            for (Dish dish : type.getDishes()) {
                //1.4 值为true代表要插入的菜品是这个店铺的，然后count减1
                if (dishIdSet.contains(dish.getDishId())){
                    count--;
                    //1.5 判断要插入的菜品是否都属于此店铺中顺便算出菜品的总价格
                    totalPrice = totalPrice + dish.getDishPrice()*dishMap.get(dish.getDishId());
                }
            }
        }
        //1.6 值为true代表count不等于0，即代表要插入的菜品中不都属于此店铺
        if ( !(count==0) ){
            return 2;
        }

        //2.计算要新添加的菜品的总价格（1.5步已算出）

        //3.判断该桌是否还存在未结账订单。如果不存在，就添加一条新的订单记录；如果存在，则再添加几条属于该订单的订单明细
        Order order1 = new Order();
        order1.setOrderShopId(shopId);
        order1.setDeskId(deskId.byteValue());
        order1.setOrderStatus((byte)0);
        List<Order> orderByCondition = orderMapper.findOrderByCondition(order1);
        //3.1该店铺该桌位不存在未结账订单
        if (orderByCondition.size()==0){
            //System.out.println(11111111);
            //4.插入订单
            //4.1 构建order对象
            Order order = new Order();
            order.setOrderShopId(shopId);
            order.setDeskId( deskId.byteValue());
            order.setOrderStatus((byte)0);
            order.setOrderDateBuy(new Date());
            order.setOrderTotalPrice(totalPrice);
            //4.2 向数据插入此订单
            orderMapper.insertOrder(order);
            //4.3 获取从数据库返回的orderId，插入订单明细需要orderId
            Integer orderId = order.getOrderId();

            //模拟出错，看事务是否有效
            //int a = 1/0;

            //5.插入订单明细和更新菜品数量
            Item item = new Item();
            for (Integer integer : dishIdSet) {
                item.setItemOrderId(orderId);
                item.setItemDishId(integer);
                item.setItemNumber(dishMap.get(integer));
                item.setItemWaitNumber(dishMap.get(integer));
                itemMapper.insertItem(item);
                dishMapper.updateDishNumber(-dishMap.get(integer),integer);

            }
        }else{
            //System.out.println(222222222);
            //3.2该店铺该桌位存在还未结账订单
            //4.更新订单总价
            orderMapper.updateOrderTotalPrice(orderByCondition.get(0).getOrderTotalPrice()+totalPrice,
                    orderByCondition.get(0).getOrderId());
            //5.插入订单明细或修改订单明细
            //5.1若原来已经点了的菜，则修改菜品数量
            for (Item item1 : orderByCondition.get(0).getItems()) {
                if (dishIdSet.contains(item1.getItemDishId())){
                    itemMapper.updateItem(item1.getItemOrderId(),
                            item1.getItemDishId(),
                            dishMap.get(item1.getItemDishId()),
                            dishMap.get(item1.getItemDishId()));
                    dishMapper.updateDishNumber(-dishMap.get(item1.getItemDishId()),item1.getItemDishId());
                    dishIdSet.remove(item1.getItemDishId());
                }
            }
            //5.2若原来没有点这个的菜，则增加一条订单明细
            Item item = new Item();
            for (Integer integer : dishIdSet) {
                item.setItemOrderId(orderByCondition.get(0).getOrderId());
                item.setItemDishId(integer);
                item.setItemNumber(dishMap.get(integer));
                item.setItemWaitNumber(dishMap.get(integer));
                itemMapper.insertItem(item);
                dishMapper.updateDishNumber(-dishMap.get(integer),integer);
            }
        }

        return 1;
    }

    /**
     * 根据order获取order信息
     * @param order
     * @return
     */
    @Override
    public List<Order> getOrder(Order order) {
        return orderMapper.findOrderByCondition(order);
    }

    /**
     * 结账（将orderStatus从0->1）
     * @param orderId
     * @return
     */
    @Override
    public Integer updateOrder(Integer orderId,Integer shopId){

        //1.构建order对象作为查询条件
        Order order = new Order();
        order.setOrderShopId(shopId);
        order.setOrderId(orderId);

        //2.判断该order是否属于此店铺
        List<Order> orderList = orderMapper.findOrderByCondition(order);
        if (orderList==null||orderList.size()==0){
            return 2;
        }

        //3.继续补充order对象
        order.setOrderStatus((byte)1);
        order.setOrderDatePay(new Date());

        //4.向数据库修改order
        return orderMapper.updateOrder(order);
    }

    /**
     * 获取某店铺所有还存在未结账订单的桌号
     * @param shopId
     * @return
     */
    @Override
    public List<Integer> getOrderDishDesks(Integer shopId) {
        return orderMapper.getOrderDishDesks(shopId);
    }
}
