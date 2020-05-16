package com.jxnu.service;

import com.jxnu.domain.Shop;

/**
 * @date 2020/4/20 9:59
 */
public interface CustomerService {

    /**
     * 根据shopId获取店铺信息
     * @param shopId
     * @return
     */
    Shop getShopInfoByShopId(Integer shopId);
}
