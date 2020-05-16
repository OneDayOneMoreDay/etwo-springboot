package com.jxnu.service.impl;

import com.jxnu.mapper.ItemMapper;
import com.jxnu.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @date 2020/5/16 20:31
 * @author dk
 * 关于订单明细的service
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemMapper itemMapper;

    /**
     * 使菜品待上数量加一或减一
     * @param shopId 要修改菜品待上数量的订单明细所属店铺的id
     * @param deskId 要修改菜品待上数量的订单明细所属桌位的id
     * @param dishId 要修改菜品待上数量的菜品id
     * @param number 该值为+1或-1，代表着菜品待上加一或减一
     * @return true 修改成功，false 修改失败
     */
    @Override
    public boolean updateItemWaitNumber(Integer shopId, Integer deskId, Integer dishId, Integer number) {
        return itemMapper.updateItemWaitNumber(shopId,deskId,dishId,number)==1 ? true : false;
    }
}
