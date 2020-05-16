package com.jxnu.service.impl;

import com.jxnu.domain.Dish;
import com.jxnu.domain.Type;
import com.jxnu.mapper.DishMapper;
import com.jxnu.mapper.ShopMapper;
import com.jxnu.mapper.TypeMapper;
import com.jxnu.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @date 2020/4/19 13:39
 */
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private TypeMapper typeMapper;

    /**
     * 添加一个菜品
     * @param dishTypeId 要添加的菜品的菜品类型id
     * @param dishName 要添加的菜品的名字
     * @param dishImgPath 要添加的菜品的图片路径
     * @param dishPrice 要添加的菜品的价格
     * @param dishNumber 要添加的菜品的数量
     * @param shopId 要添加的菜品的所属店铺id
     * @return 1 插入成功；2 此店铺不存在该dishTypeId，插入失败；
     *         3 店铺是否已经存在同名的菜品，插入失败;其它任何值 插入失败
     */
    @Override
    public Integer insertDish(Integer dishTypeId, String dishName, String dishImgPath,
                              Float dishPrice, Integer dishNumber, Integer shopId) {

        //1.判断该店铺是否已经有此dishTypeId
        boolean bool = false;
        List<Type> typeList = typeMapper.findTypeByShopId(shopId);
        for (Type type : typeList) {
            if (dishTypeId.equals(type.getTypeId())){
                bool = true;
                break;
            }
        }
        if (!bool){
            return 2;
        }

        //2.判断该店铺是否已经存在同名的菜品了
        for (Type type : typeList) {
            for (Dish dish : type.getDishes()) {
                if (Objects.equals(dishName,dish.getDishName())){
                    return 3;
                }
            }
        }

        //2.向数据库插入一条菜品信息
        Dish dish = new Dish();
        dish.setDishTypeId(dishTypeId);
        dish.setDishName(dishName);
        dish.setDishImgPath(dishImgPath);
        dish.setDishPrice(dishPrice);
        dish.setDishNumber(dishNumber);
        dish.setDishStatus((byte)1);

        return dishMapper.insertDish(dish);
    }

    /**
     * 根据菜品类型（dishTypeID）查询正常销售的菜品信息
     * @param shopId
     * @return
     */
    @Override
    public List<Dish> findDishByDishTypeId(Integer shopId) {
        return dishMapper.findDishByDishTypeId(shopId);
    }

    @Override
    public Integer deleteDish(Integer dishId, Integer shopId) {

        //1.判断此dishId是否属于此店铺
        boolean bool = false;
        List<Type> typeList = typeMapper.findTypeByShopId(shopId);
        for (Type type1 : typeList) {
            for (Dish dish : type1.getDishes()) {
                if (Objects.equals(dishId,dish.getDishId())){
                    bool = true;
                    break;
                }
            }
            if (bool){
                break;
            }
        }
        if (!bool){
            return 2;
        }
        //2.从数据库删除此菜品
        return dishMapper.deleteDish(dishId);
    }

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
    @Override
    public Integer updateDish(Integer dishId, Integer dishTypeId, String dishName, Float dishPrice, Integer dishNumber, Integer shopId) {
        //1.判断此dishId是否属于此店铺
        boolean bool = false;
        List<Type> typeList = typeMapper.findTypeByShopId(shopId);
        for (Type type1 : typeList) {
            for (Dish dish : type1.getDishes()) {
                if (Objects.equals(dishId,dish.getDishId())){
                    bool = true;
                    break;
                }
            }
            if (bool){
                break;
            }
        }
        if (!bool){
            return 2;
        }

        //2.判断此dishTypeId是否属于此店铺
        bool = false;
        for (Type type : typeList) {
            if (dishTypeId.equals(type.getTypeId())){
                bool = true;
                break;
            }
        }
        if (!bool){
            return 3;
        }

        //2.判断该店铺是否已经存在同名的菜品了
        for (Type type : typeList) {
            for (Dish dish : type.getDishes()) {
                if (Objects.equals(dishName,dish.getDishName())){
                    return 4;
                }
            }
        }

        //3.从数据库修改此菜品
        Dish dish = new Dish();
        dish.setDishId(dishId);
        dish.setDishTypeId(dishTypeId);
        dish.setDishName(dishName);
        dish.setDishPrice(dishPrice);
        dish.setDishNumber(dishNumber);

        return dishMapper.updateDish(dish);
    }

    /**
     * 根据菜品id查询菜品信息
     * @param dishId
     * @return
     */
    public Dish findDishByDishId(Integer dishId){
        return dishMapper.findDishByDishId(dishId);
    };
}
