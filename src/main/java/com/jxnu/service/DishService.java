package com.jxnu.service;

import com.jxnu.domain.Dish;

import java.util.List;

/**
 * @date 2020/4/19 13:34
 */
public interface DishService {

    /**
     * 添加一个菜品
     * @param dishTypeId 要添加的菜品的菜品类型id
     * @param dishName 要添加的菜品的名字
     * @param dishImgPath 要添加的菜品的图片路径
     * @param dishPrice 要添加的菜品的价格
     * @param dishNumber 要添加的菜品的数量
     * @param shopId 要添加的菜品的所属店铺id
     * @return 1 插入成功；2 此店铺不存在该dishTypeId，插入失败；其它任何值 插入失败
     */
    Integer insertDish(Integer dishTypeId,String dishName,String dishImgPath,
                       Float dishPrice,Integer dishNumber,Integer shopId);

    /**
     * 根据菜品类型（dishTypeID）查询正常销售的菜品信息
     * @param shopId
     * @return
     */
    List<Dish> findDishByDishTypeId(Integer shopId);

    /**
     * 根据菜品id来删除菜品
     * @param dishId 要删除的菜品的id
     * @param shopId 要删除的菜品的所属店铺的id
     * @return 1 删除成功；2 该dishId不属于此店铺；其它任何值 删除失败
     */
    Integer deleteDish(Integer dishId,Integer shopId);

    /**
     * 修改一个菜品
     * @param dishId 要修改的菜品的菜品id
     * @param dishTypeId 要修改的菜品的菜品类型id
     * @param dishName 要修改的菜品的名字
     * @param dishPrice 要修改的菜品的价格
     * @param dishNumber 要修改的菜品的数量
     * @param shopId 要修改的菜品的所属店铺id
     * @return 1 修改成功；2 此店铺不存在该dishId，修改失败；3 此店铺不存在该dishTypeId，修改失败；
     *         4 店铺是已经存在同名的菜品，修改失败;其它任何值 修改失败
     */
    Integer updateDish(Integer dishId,Integer dishTypeId,String dishName,
                       Float dishPrice,Integer dishNumber,Integer shopId);

    /**
     * 根据菜品id查询菜品信息
     * @param dishId
     * @return
     */
    Dish findDishByDishId(Integer dishId);
}
