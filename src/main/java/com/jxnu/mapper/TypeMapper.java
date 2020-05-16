package com.jxnu.mapper;

import com.jxnu.domain.Type;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author dk
 * @version 1.0
 * @date 2020/3/17 14:30
 */
@Repository
public interface TypeMapper {

    /**
     * 根据shopId查询菜品分类信息
     * @return
     */
    List<Type> findTypeByShopId(Integer shopId);

    /**
     * 添加一条菜品分类
     * @return 数据库受影响的行数
     */
    Integer insertType(Type type);

    /**
     * 修改一条菜品分类
     * @param type
     * @return
     */
    Integer updateType(Type type);

    /**
     * 删除一条菜品分类
     * @param typeId
     * @return
     */
    Integer deleteType(Integer typeId);
}
