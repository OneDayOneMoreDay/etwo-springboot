package com.jxnu.controller;

import com.jxnu.domain.Dish;
import com.jxnu.domain.Shop;
import com.jxnu.domain.Type;
import com.jxnu.service.TypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @date 2020/4/17 19:12
 * 关于菜品分类的controller
 * <p>
 * 获取一家店铺的所有的菜品分类以及菜品
 * 获取一家店铺的所有的菜品分类
 * 添加一条菜品分类
 * 修改一条菜品分类
 * 删除一条菜品分类
 */
@Controller
@ResponseBody
public class TypeController {

    @Autowired
    private TypeService typeService;

    /**
     * 获取一家店铺的菜品分类以及菜品
     *
     * @param session
     * @return
     */
    @RequestMapping(path = "/typeAndDish")
    public Map<String, Object> typeAndDish(HttpSession session) {

        Logger logger = LoggerFactory.getLogger(ShopController.class);
        logger.info("typeAndDishSession = " + session);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.从session中获取已经登录的shop对象
        Shop shopFromSession = (Shop) session.getAttribute("shop");
        logger.info("shopFromSession = " + shopFromSession);
        if (shopFromSession == null) {
            map.put("success", false);
            map.put("msg", "请先登录");
            return map;
        }

        //2.获取数据
        List<Type> typeList = typeService.findTypeByShopId(shopFromSession.getShopId());

        //权宜之计，mybatis延迟加载导致返回json错误
        List<Type> types1 = new ArrayList<Type>();
        for (Type type : typeList) {
            Type type1 = new Type();
            type1.setTypeId(type.getTypeId());
            type1.setTypeName(type.getTypeName());
            type1.setTypeShopId(type.getTypeShopId());
            type1.setDishes(type.getDishes());
            types1.add(type1);
        }

        //3.返回结果
        map.put("success", true);
        map.put("typeList", types1);
        return map;
    }

    /**
     * 获取一家店铺的菜品分类
     *
     * @param session
     * @return
     */
    @RequestMapping(path = "/type")
    public Map<String, Object> type(HttpSession session) {

        Logger logger = LoggerFactory.getLogger(ShopController.class);
        logger.info("typeSession = " + session);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.从session中获取已经登录的shop对象
        Shop shopFromSession = (Shop) session.getAttribute("shop");
        logger.info("shopFromSession = " + shopFromSession);
        if (shopFromSession == null) {
            map.put("success", false);
            map.put("msg", "请先登录");
            return map;
        }

        //2.获取数据
        List<Type> typeList = typeService.findTypeByShopId(shopFromSession.getShopId());

        //权宜之计，mybatis延迟加载导致返回json错误
        List<Type> types1 = new ArrayList<Type>();
        for (Type type : typeList) {
            Type type1 = new Type();
            type1.setTypeId(type.getTypeId());
            type1.setTypeName(type.getTypeName());
            type1.setTypeShopId(type.getTypeShopId());

            types1.add(type1);
        }

        //3.返回结果
        map.put("success", true);
        map.put("typeList", types1);
        return map;
    }

    /**
     * 添加一条菜品分类
     *
     * @param session
     * @param typeName 要添加的菜品分类的名字
     * @return
     */
    @RequestMapping(path = "/addType", params = "typeName")
    public Map<String, Object> addType(HttpSession session, String typeName) {
        Logger logger = LoggerFactory.getLogger(ShopController.class);
        logger.info("addTypeSession = " + session);
        logger.info("typeName = " + typeName);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.从session中获取已经登录的shop对象
        Shop shopFromSession = (Shop) session.getAttribute("shop");
        logger.info("shopFromSession = " + shopFromSession);
        if (shopFromSession == null) {
            map.put("success", false);
            map.put("msg", "请先登录");
            return map;
        }

        //2.防止菜品分类名为""
        if ("" == typeName.trim()) {
            map.put("success", false);
            map.put("msg", "请输入有效字符");
            return map;
        }

        //3.从数据库添加菜品分类
        Integer i = typeService.insertType(typeName, shopFromSession.getShopId());

        //4.添加失败的两种情况
        if (i == 2) {
            map.put("success", false);
            map.put("msg", "该菜品分类名已经存在，请勿重复添加");
            return map;
        }
        if (i != 2 && i != 1) {
            map.put("success", false);
            map.put("msg", "添加失败");
            return map;
        }

        //5.添加成功
        map.put("success", true);
        map.put("msg", "添加菜品分类成功");
        return map;
    }

    /**
     * 修改一条菜品分类
     *
     * @param session
     * @param typeName 要修改的菜品分类的名字
     * @return
     */
    @RequestMapping(path = "/updateType", params = {"typeName", "typeId"})
    public Map<String, Object> updateType(HttpSession session, String typeName, Integer typeId) {
        Logger logger = LoggerFactory.getLogger(ShopController.class);
        logger.info("updateTypeSession = " + session);
        logger.info("typeName = " + typeName);
        logger.info("typeId = " + typeId);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.从session中获取已经登录的shop对象
        Shop shopFromSession = (Shop) session.getAttribute("shop");
        logger.info("shopFromSession = " + shopFromSession);
        if (shopFromSession == null) {
            map.put("success", false);
            map.put("msg", "请先登录");
            return map;
        }

        //2.防止菜品分类名为""
        if ("" == typeName.trim()) {
            map.put("success", false);
            map.put("msg", "请输入有效字符");
            return map;
        }

        //3.从数据库修改菜品分类
        Integer i = typeService.updateType(typeName, typeId, shopFromSession.getShopId());

        //4.添加失败的三种情况
        if (i == 3) {
            map.put("success", false);
            map.put("msg", "此店铺不存在此菜品分类id");
            return map;
        }
        if (i == 2) {
            map.put("success", false);
            map.put("msg", "该菜品分类名已经存在，修改失败");
            return map;
        }
        if (i != 3 && i != 2 && i != 1) {
            map.put("success", false);
            map.put("msg", "修改失败");
            return map;
        }

        //5.添加成功
        map.put("success", true);
        map.put("msg", "修菜品分类成功");
        return map;
    }

    /**
     * 删除一条菜品分类
     *
     * @param session
     * @param typeId  要删除的菜品分类的id
     * @return
     */
    @RequestMapping(path = "/deleteType", params = {"typeId"})
    public Map<String, Object> deleteType(HttpSession session, Integer typeId) {
        Logger logger = LoggerFactory.getLogger(ShopController.class);
        logger.info("deleteTypeSession = " + session);
        logger.info("typeId = " + typeId);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.从session中获取已经登录的shop对象
        Shop shopFromSession = (Shop) session.getAttribute("shop");
        logger.info("shopFromSession = " + shopFromSession);
        if (shopFromSession == null) {
            map.put("success", false);
            map.put("msg", "请先登录");
            return map;
        }

        //2.从数据库删除菜品分类
        Integer i = typeService.deleteType(typeId, shopFromSession.getShopId());

        //3.添加失败的三种情况
        if (i == 3) {
            map.put("success", false);
            map.put("msg", "该店铺不存在此typeId,修改失败");
            return map;
        }
        if (i == 2) {
            map.put("success", false);
            map.put("msg", "该菜品分类下还有菜品，请先删除菜品再删除该分类");
            return map;
        }
        if (i != 3 && i != 2 && i != 1) {
            map.put("success", false);
            map.put("msg", "删除失败");
            return map;
        }

        //4.添加成功
        map.put("success", true);
        map.put("msg", "删除品分类成功");
        return map;
    }
}
