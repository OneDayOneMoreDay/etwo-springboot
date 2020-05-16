package com.jxnu.service;

import com.jxnu.domain.Type;

import java.util.List;

/**
 * @author dk
 * @version 1.0
 * @date 2020/3/26 16:34
 */
public interface TypeService {

    /**
     * 根据shopId查询菜品分类信息
     * @return
     */
    List<Type> findTypeByShopId(Integer shopId);

    /**
     * 添加一条菜品分类
     * @param typeName 新添加菜品分类名
     * @param shopId 该菜品分类所属的店铺id
     * @return 1 修改成功，2 已经有相同名字的菜品分类了，其它任何值 修改失败
     */
    Integer insertType(String typeName,Integer shopId);

    /**
     * 修改一条菜品分类
     * @param typeName 新的菜品分类名
     * @param typeId 要修改的菜品分类的id
     * @param shopId 该菜品分类所属的店铺id
     * @return 1 修改成功，2 已经有相同名字的菜品分类了，修改失败，3 在该店铺不存在此typeId,修改失败，其它任何值 修改失败
     */
    Integer updateType(String typeName,Integer typeId,Integer shopId);

    /**
     * 删除一条菜品分类
     * @param typeId 要删除的菜品分类的id
     * @param shopId 该菜品分类所属的店铺id
     * @return 1 删除成功，2 该菜品分类下还有菜品，删除失败，3 在该店铺不存在此typeId,删除失败，其它任何值 删除失败
     */
    Integer deleteType(Integer typeId,Integer shopId);
}
