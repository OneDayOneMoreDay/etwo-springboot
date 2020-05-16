package com.jxnu.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @author dk
 * @version 1.0
 * @date 2020/3/17 9:01
 */
@Data
public class Dish implements Serializable {

    //菜品编号
    private Integer dishId;
    //菜品类型编号
    private Integer dishTypeId;
    //菜品名
    private String dishName;
    //菜品图片路径
    private String dishImgPath;
    //菜品价格
    private Float dishPrice;
    //菜品数量
    private Integer dishNumber;
    //菜品状态(0被删除，1正常销售)
    private Byte dishStatus;
}
