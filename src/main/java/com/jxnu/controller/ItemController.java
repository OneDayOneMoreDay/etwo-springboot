package com.jxnu.controller;

import com.jxnu.domain.Shop;
import com.jxnu.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @date 2020/5/16 22:51
 *
 * 跟订单明细有关的controller
 */
@RestController
@RequestMapping({"/item"})
public class ItemController {

    @Autowired
    private ItemService itemService;

    private Logger logger = LoggerFactory.getLogger(ItemController.class);

    /**
     * 修改某个菜的待上数量（+1或者-1）
     * @param req
     * @param deskId 要修改菜品待上数量的订单明细所属桌位的id
     * @param dishId 要修改菜品待上数量的菜品id
     * @param incrOrReduce 该值为true或false，为true代表使某个菜的待上数量+1，为false代表使某个菜的待上数量-1
     * @return
     */
    @GetMapping(path = "/updateItemWaitNumber",params = {"deskId","dishId","incrOrReduce"})
    public Map<String, Object> reduceItemWaitNumber(HttpServletRequest req,
                                                    Integer deskId, Integer dishId, boolean incrOrReduce) {
        Logger logger = LoggerFactory.getLogger(ShopController.class);
        logger.info("deskId = " + deskId);
        logger.info("dishId = " + dishId);
        logger.info("incrOrReduce = " + incrOrReduce);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.从session中获取已经登录的shop对象
        Shop shopFromSession = (Shop) req.getSession().getAttribute("shop");
        logger.info("shopBySession = " + shopFromSession);

        //2.调用service层方法
        boolean b = itemService.updateItemWaitNumber(shopFromSession.getShopId(), deskId, dishId, incrOrReduce ? 1 : -1);

        if (!b){
            map.put("success", false);
            map.put("msg", "修改待上菜品数量失败");
            return map;
        }
        map.put("success", true);
        map.put("msg", "修改待上菜品数量成功");
        return map;
    }
}
