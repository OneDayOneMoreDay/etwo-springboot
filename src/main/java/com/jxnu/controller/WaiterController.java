package com.jxnu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxnu.domain.Shop;
import com.jxnu.service.ItemService;
import com.jxnu.service.ShopService;
import com.jxnu.utils.VerifyCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dk
 * 关于服务员的controller
 * @date 2020/5/16 19:19
 */
@RestController
@RequestMapping({"/waiter"})
public class WaiterController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private ItemService itemService;

    private Logger logger = LoggerFactory.getLogger(WaiterController.class);

    /**
     * 服务员登录（服务员登录与店铺登录是相同的邮箱、密码）
     *
     * @param req      HttpServletRequest,为了设置session
     * @param resp     HttpServletResponse,为了设置cookie
     * @param email    店铺邮箱号
     * @param password 店铺密码
     * @param remember 是否启用免登录,false 不启用，true 启用，默认值 false，可选
     * @return 根据是否登录成功返回相应的json提示信息
     */
    @PostMapping(path = "/login", params = {"email", "password"})
    public Map<String, Object> login(HttpServletRequest req, HttpServletResponse resp,
                                     String email, String password,
                                     @RequestParam(defaultValue = "false") Boolean remember) {

        //用slf4J和log4J日志框架输出接收到的参数
        Logger logger = LoggerFactory.getLogger(ShopController.class);
        logger.info("email = " + email);
        logger.info("password = " + password);
        logger.info("remember = " + remember);

        Map<String, Object> map = new HashMap<String, Object>();

        //2.判断邮箱和密码是否正确
        Shop shop = shopService.login(email, password);
        if (shop == null) {
            map.put("success", false);
            map.put("msg", "登录失败，邮箱或密码错误");
            return map;
        }

        //3.邮箱和密码正确,登录成功,设置cookie
        if (remember) {
            ObjectMapper om = new ObjectMapper();
            String str_shop = null;
            try {
                //3.1将shop对象转化为json字符串
                str_shop = om.writeValueAsString(shop);
                logger.info("str_shop编码前 = " + str_shop);
                //3.2将由shop对象转化而来的json字符串进行url方式的编码，因为其含有双引号，放入cookie会报错
                str_shop = URLEncoder.encode(str_shop, "utf-8");
                logger.info("str_shop编码后 = " + str_shop);
            } catch (Exception e) {
                e.printStackTrace();
                map.put("success", false);
                map.put("msg", "登录失败");
                return map;
            }
            Cookie cookie = new Cookie("shop", str_shop);
            //3.3设置cookie的有效范围为该服务器下的所有路径
            cookie.setPath("/");
            //3.4设置cookie有效期为12小时
            cookie.setMaxAge(60 * 60 * 12);
            resp.addCookie(cookie);
        }

        //4.将shop对象放入session中
        HttpSession session = req.getSession();
        session.setAttribute("shop", shop);

        //5.返回数据
        map.put("success", true);
        map.put("msg", "登录成功");
        return map;
    }

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
                                                    Integer deskId,Integer dishId,boolean incrOrReduce) {
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
