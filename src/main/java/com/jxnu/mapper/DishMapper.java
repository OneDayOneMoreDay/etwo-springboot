package com.jxnu.mapper;

import com.jxnu.domain.Dish;
import com.jxnu.domain.Shop;
import org.omg.PortableInterceptor.Interceptor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author dk
 * @version 1.0
 * @date 2020/3/17 15:08
 */
@Repository
public interface DishMapper {

    /**
     * 根据菜品类型查询正常销售的菜品信息
     * @return
     */
    List<Dish> findDishByDishTypeId(Integer dishTypeId);

    /**
     * 添加一个菜品
     * @param dish 要添加的菜品对象
     * @return
     */
    Integer insertDish(Dish dish);

    /**
     * 根据菜品id来删除菜品
     * @param dishId
     * @return
     */
    Integer deleteDish(Integer dishId);

    /**
     * 修改一个菜品
     * @param dish
     * @return
     */
    Integer updateDish(Dish dish);

    /**
     * 根据菜品id查询菜品信息
     * @param dishId
     * @return
     */
    Dish findDishByDishId(Integer dishId);
}
