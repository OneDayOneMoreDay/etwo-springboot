package com.jxnu.service;

import com.jxnu.domain.Shop;

import java.util.List;

/**
 * @author dk
 * @version 1.0
 * @date 2020/3/23 21:50
 */
public interface ShopService {

    /**
     * 店铺登录
     * @param shopEmail
     * @param shopPassword
     * @return 登录成功返回Shop对象，登录失败返回null
     */
    Shop login(String shopEmail,String shopPassword);

    /**
     * 店铺注册
     * @param shopEmail
     * @param shopPassword
     * @param shopName
     * @param shopAddress
     * @param shopImgPath
     * @param shopNotice
     * @return 注册成功返回true，注册失败返回false
     */
    boolean register(String shopEmail,String shopPassword,String shopName,String shopAddress,
                     String shopImgPath,String shopNotice);

    /**
     * 根据邮箱号查询店铺信息
     * @param shopEmail
     * @return 查询成功返回shop对象，查询失败返回null
     */
    Shop findShopByEmail(String shopEmail);

    Integer updateShop(String shopName, String shopAddress, String shopNotice,Integer shopId);

    Integer updateShopPassword(Integer shopId,String shopPassword);
}
