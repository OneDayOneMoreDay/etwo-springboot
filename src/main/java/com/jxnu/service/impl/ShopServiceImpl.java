package com.jxnu.service.impl;

import com.jxnu.domain.Shop;
import com.jxnu.mapper.ShopMapper;
import com.jxnu.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author dk
 * @version 1.0
 * @date 2020/3/23 23:27
 */
@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopMapper shopMapper;

    /**
     * 店铺登录
     * @param shopEmail
     * @param shopPassword
     * @return 登录成功返回Shop对象，登录失败返回null
     */
    @Override
    public Shop login(String shopEmail, String shopPassword) {

        Shop shop1 = new Shop();
        shop1.setShopEmail(shopEmail);
        shop1.setShopPassword(shopPassword);

        //1.根据邮箱和密码从数据库里面查询店铺信息
        Shop shop2 = shopMapper.findShopByCondition(shop1);

        //2.返回shop对象
        return shop2;
    }

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
    @Override
    public boolean register(String shopEmail,String shopPassword,String shopName,String shopAddress,
                            String shopImgPath,String shopNotice) {

        Shop shop1 = new Shop();
        shop1.setShopEmail(shopEmail);

        //1.根据邮箱从数据库里面查询店铺信息
        Shop shop2 = shopMapper.findShopByCondition(shop1);

        //2.查询到信息，代表该邮箱已经注册过了，则注册失败
        if (shop2!=null)
            return false;

        //3.未查询到信息，代表该邮箱未注册过，则向数据库插入一条店铺信息
        shop1.setShopPassword(shopPassword);
        shop1.setShopName(shopName);
        shop1.setShopAddress(shopAddress);
        shop1.setShopImgPath(shopImgPath);
        shop1.setShopNotice(shopNotice);
        shop1.setShopStatus((byte)1);
        Integer i = shopMapper.insertShop(shop1);

        //4.插入信息失败，则注册失败
        if (i!=1){
            return false;
        }

        //4.插入信息成功，则注册成功
        return true;
    }

    /**
     * 根据邮箱号查询店铺信息
     * @param shopEmail
     * @return 查询成功返回shop对象，查询失败返回null
     */
    @Override
    public Shop findShopByEmail(String shopEmail) {

        Shop shop1 = new Shop();
        shop1.setShopEmail(shopEmail);

        //1.根据邮箱从数据库里面查询店铺信息
        return shopMapper.findShopByCondition(shop1);
    }

    @Override
    public Integer updateShop(String shopName, String shopAddress, String shopNotice, Integer shopId) {
        Shop shop = new Shop();
        shop.setShopId(shopId);
        shop.setShopName(shopName);
        shop.setShopAddress(shopAddress);
        shop.setShopNotice(shopNotice);
        return shopMapper.updateShop(shop);
    }

    @Override
    public Integer updateShopPassword(Integer shopId, String shopPassword) {
        Shop shop = new Shop();
        shop.setShopId(shopId);
        shop.setShopPassword(shopPassword);
        return shopMapper.updateShopPassword(shop);
    }

    @Override
    public Integer updateShopPassword(String shopEmail, String shopPassword) {
        Shop shop = new Shop();
        shop.setShopEmail(shopEmail);
        shop.setShopPassword(shopPassword);
        return shopMapper.updateShopPassword(shop);
    }
}
