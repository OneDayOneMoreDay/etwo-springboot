package com.jxnu.mapper;

import com.jxnu.domain.Shop;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author dk
 * @version 1.0
 * @date 2020/3/17 12:35
 */
@Repository
public interface ShopMapper {

    /**
     * 根据传入参数条件查询店铺，延迟加载
     * @param shop 查询的条件：有可能只有邮箱，还有可能只有邮箱和密码.....
     * @return
     */
    Shop findShopByCondition(Shop shop);

    /**
     * 插入一家店铺
     * @param shop 要插入的店铺
     * @return
     */
    Integer insertShop(Shop shop);


    /**
     * 修改一家店铺信息
     * @param shop 要修改的店铺
     * @return
     */
    Integer updateShop(Shop shop);

    /**
     * 修改店铺密码
     * @param shop 要修改的店铺
     * @return
     */
    Integer updateShopPassword(Shop shop);
}
