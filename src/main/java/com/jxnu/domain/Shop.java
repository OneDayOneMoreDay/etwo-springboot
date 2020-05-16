package com.jxnu.domain;

import lombok.Data;

/**
 * @author dk
 * @version 1.0
 * @date 2020/3/17 8:51
 */
@Data
public class Shop {

    //店铺编号
    private Integer shopId;
    //店铺名
    private String shopName;
    //店铺地址
    private String shopAddress;
    //店铺图片路径
    private String shopImgPath;
    //店铺公告
    private String shopNotice;
    //店铺状态(忽略这个字段)
    private Byte shopStatus;
    //店铺邮箱
    private String shopEmail;
    //店铺密码
    private String shopPassword;
}
