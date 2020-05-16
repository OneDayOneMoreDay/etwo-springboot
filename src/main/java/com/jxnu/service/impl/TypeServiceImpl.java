package com.jxnu.service.impl;

import com.jxnu.domain.Dish;
import com.jxnu.domain.Type;
import com.jxnu.mapper.DishMapper;
import com.jxnu.mapper.TypeMapper;
import com.jxnu.service.TypeService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author dk
 * @version 1.0
 * @date 2020/3/26 16:35
 */
@Service
public class TypeServiceImpl implements TypeService {

    @Autowired
    private TypeMapper typeMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 根据shopId查询菜品分类信息
     *
     * @return
     */
    @Override
    public List<Type> findTypeByShopId(Integer shopId) {
        return typeMapper.findTypeByShopId(shopId);
    }

    /**
     * 添加一条菜品分类
     *
     * @param typeName 新添加菜品分类名
     * @param shopId   该菜品分类所属的店铺id
     * @return 1 添加刚才，2 已经有相同名字的菜品分类了，添加失败，其它任何值 添加失败
     */
    @Override
    public Integer insertType(String typeName, Integer shopId) {
        Type type = new Type();
        type.setTypeName(typeName);
        type.setTypeShopId(shopId);

        //判断这个名字的菜品分类是否已经存在
        List<Type> typeList = typeMapper.findTypeByShopId(shopId);
        for (Type type1 : typeList) {
            if (Objects.equals(typeName, type1.getTypeName())) {
                return 2;
            }
        }
        return typeMapper.insertType(type);
    }

    /**
     * 修改一条菜品分类
     * @param typeName 新的菜品分类名
     * @param typeId 要修改的菜品分类的id
     * @param shopId 该菜品分类所属的店铺id
     * @return 1 修改成功，2 已经有相同名字的菜品分类了，3 在该店铺不存在此typeId,修改失败，其它任何值 修改失败
     */
    @Override
    public Integer updateType(String typeName, Integer typeId,Integer shopId) {
        Type type = new Type();
        type.setTypeName(typeName);
        type.setTypeId(typeId);

        List<Type> typeList = typeMapper.findTypeByShopId(shopId);

        //判断该店铺是否存在此typeId
        boolean bool = true;
        for (Type type1 : typeList) {
            if (Objects.equals(typeId, type1.getTypeId())) {
                bool = false;
            }
        }
        if (bool){
            return 3;
        }

        //判断这个名字的菜品分类是否已经存在
        for (Type type1 : typeList) {
            if (Objects.equals(typeName, type1.getTypeName())) {
                return 2;
            }
        }

        return typeMapper.updateType(type);
    }

    /**
     * 删除菜品分类
     * @param typeId 要删除的菜品分类的id
     * @param shopId 该菜品分类所属的店铺id
     * @return 1 删除成功，2 该菜品分类下还有菜品，删除失败，3 在该店铺不存在此typeId,删除失败，其它任何值 删除失败
     */
    @Override
    public Integer deleteType(Integer typeId,Integer shopId) {

        //判断该店铺是否存在此typeId
        List<Type> typeList = typeMapper.findTypeByShopId(shopId);
        boolean bool = true;
        for (Type type1 : typeList) {
            if (Objects.equals(typeId, type1.getTypeId())) {
                bool = false;
            }
        }
        if (bool){
            return 3;
        }

        List<Dish> dishList = dishMapper.findDishByDishTypeId(typeId);
        if (dishList.size()!=0){
            return 2;
        }
        return typeMapper.deleteType(typeId);
    }
}
