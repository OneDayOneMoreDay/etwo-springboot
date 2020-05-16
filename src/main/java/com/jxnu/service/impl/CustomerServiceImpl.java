package com.jxnu.service.impl;

import com.jxnu.domain.Shop;
import com.jxnu.mapper.ShopMapper;
import com.jxnu.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @date 2020/4/20 10:01
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private ShopMapper shopMapper;

    /**
     * 根据shopId获取店铺信息
     * @param shopId
     * @return
     */
    @Override
    public Shop getShopInfoByShopId(Integer shopId) {
        Shop shop = new Shop();
        shop.setShopId(shopId);
        return shopMapper.findShopByCondition(shop);
    }
}
