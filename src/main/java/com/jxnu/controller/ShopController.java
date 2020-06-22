package com.jxnu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxnu.domain.Shop;
import com.jxnu.service.CustomerService;
import com.jxnu.service.EmailService;
import com.jxnu.service.ShopService;
import com.jxnu.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @date 2020/4/13 14:14
 * 关于店铺的controller
 * <p>
 * 登录
 * 获取注册邮箱验证码
 * 注册(包含上传店铺图片)
 * 修改店铺信息(包含修改店铺图片)
 * 修改店铺密码
 * 获取店铺信息
 * 退出登录
 */
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${shopImgPath}")
    private String shopImgPath;

    @Value("${QRC.prefix}")
    private String QRCPrefix;

    @Value("${QRC.suffix}")
    private String QRCSuffix;

    /**
     * 登录
     * 开放get方法测试
     *
     * @param req      HttpServletRequest
     * @param resp     HttpServletResponse,为了设置cookie
     * @param email    店铺邮箱号
     * @param password 店铺密码
     * @param code     登录的图片验证码
     * @param remember 是否启用免登录,false 不启用，true 启用，默认值 false，可选
     * @return 根据是否登录成功返回相应的json提示信息
     */
    @RequestMapping(path = "/login", method = {RequestMethod.POST, RequestMethod.GET},
            params = {"email", "password", "code"})
    public Map<String, Object> login(HttpServletRequest req, HttpServletResponse resp,
                                     String email, String password, String code,
                                     @RequestParam(defaultValue = "false") Boolean remember) {
        //用slf4J和log4J日志框架输出接收到的参数
        Logger logger = LoggerFactory.getLogger(ShopController.class);
        logger.info("email = " + email);
        logger.info("password = " + password);
        logger.info("code = " + code);
        logger.info("remember = " + remember);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.判断验证码是否正确
        boolean b = VerifyCodeUtil.checkVerifyCode(req, code);
        if (!b) {
            map.put("success", false);
            map.put("msg", "登录失败，验证码错误");
            return map;
        }

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
        }else{
            // 删除原来可能已有的cookie
            Cookie cookie = new Cookie("shop", "");
            cookie.setPath("/");
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
     * 获取注册邮箱验证码
     *
     * @param req
     * @param email
     * @return 根据是否发送注册验证码成功返回相应的json提示信息
     */
    @GetMapping(path = "/getEmailCode", params = "email")
    public Map<String, Object> getEmailCode(HttpServletRequest req, String email) {

        //用slf4J和log4J日志框架输出接收到的参数
        Logger logger = LoggerFactory.getLogger(ShopController.class);
        logger.info("email = " + email);

        Map<String, Object> map = new HashMap<>();

        //1.判断邮箱是否已经注册
        Shop shop = shopService.findShopByEmail(email);
        if (shop != null) {
            map.put("success", false);
            map.put("msg", "该邮箱已注册");
            return map;
        }

        //2.发送注册邮箱验证码
        String emailCode = EmailCodeUtil.getCode();
        //旧方法发送邮件验证码
        /*boolean b = EmailCodeUtil.sendEmail(email, emailCode);
        if (!b) {
            map.put("success", false);
            map.put("msg", "发送失败");
            return map;
        }*/
        //springboot整合发送发送邮件验证码
        try {
            emailService.sendEmailVerCode(email, emailCode);
        } catch (Exception e) {
            map.put("success", false);
            map.put("msg", "发送失败");
            return map;
        }


        //3.发送成功
        //3.1将邮箱验证码放入redis缓存中
        stringRedisTemplate.opsForValue().set("regCode:" + email, emailCode);
        stringRedisTemplate.expire("regCode:" + email, 70, TimeUnit.SECONDS);

        //3.1将邮箱验证码放入session缓存中
        /*HttpSession session = req.getSession();
        logger.info("getEmailCodeSession = "+session);
        session.setAttribute("emailCode", emailCode);
        session.setAttribute("email", email);
        session.setMaxInactiveInterval(60); //单位为秒*/

        map.put("success", true);
        map.put("msg", "发送成功，请注意查收");
        return map;
    }

    /**
     * 获取忘记密码时重置密码的邮箱验证码
     *
     * @param req
     * @param email 邮箱号
     * @return
     */
    @GetMapping(path = "/getForgetPasswordEmailCode", params = "email")
    public Map<String, Object> getForgetPasswordEmailCode(HttpServletRequest req, String email) {

        //用slf4J和log4J日志框架输出接收到的参数
        Logger logger = LoggerFactory.getLogger(ShopController.class);
        logger.info("email = " + email);

        Map<String, Object> map = new HashMap<>();

        //0.判断邮箱是否已经注册
        Shop shop = shopService.findShopByEmail(email);
        if (shop == null) {
            map.put("success", false);
            map.put("msg", "该邮箱未注册");
            return map;
        }

        //1.发送注册邮箱验证码
        String emailCode = EmailCodeUtil.getCode();
        try {
            emailService.sendEmailVerCode(email, emailCode);
        } catch (Exception e) {
            map.put("success", false);
            map.put("msg", "发送失败");
            return map;
        }

        //2.发送成功
        //2.1将邮箱验证码放入redis缓存中
        stringRedisTemplate.opsForValue().set("forgetPasswordEmailCode:" + email, emailCode);
        stringRedisTemplate.expire("forgetPasswordEmailCode:" + email, 70, TimeUnit.SECONDS);

        map.put("success", true);
        map.put("msg", "发送成功，请注意查收");
        return map;
    }

    /**
     * 重置密码
     *
     * @param req
     * @param email                   邮箱号
     * @param newPassword             新密码
     * @param forgetPasswordEmailCode 邮件验证码
     * @return
     */
    @PostMapping(path = "/resetPassword", params = {"email","newPassword","forgetPasswordEmailCode"})
    public Map<String, Object> resetPassword(HttpServletRequest req, String email, String newPassword,
                                             String forgetPasswordEmailCode) {
        Logger logger = LoggerFactory.getLogger(ShopController.class);
        logger.info("email = {}", email);
        logger.info("newPassword = {}", newPassword);
        logger.info("forgetPasswordEmailCode = {}", forgetPasswordEmailCode);

        Map<String, Object> map = new HashMap<String, Object>();

        //0.防止一些数据为""
        if ("".equals(email) || "".equals(newPassword)) {
            map.put("success", false);
            map.put("msg", "请输入有效数据，请重新输入");
            return map;
        }

        //1.判断注册邮箱验证码是否正确
        //1.1 从redis获取真正的邮箱验证码值
        String realEmailCode = stringRedisTemplate.opsForValue().get("forgetPasswordEmailCode:" + email);
        stringRedisTemplate.delete("forgetPasswordEmailCode:" + email);
        logger.info("realEmailCode = " + realEmailCode);
        if (!forgetPasswordEmailCode.equals(realEmailCode)) {
            map.put("success", false);
            map.put("msg", "重置密码失败，邮箱验证码输入错误");
            return map;
        }

        //2.调用service层方法
        shopService.updateShopPassword(email, newPassword);

        map.put("success", true);
        map.put("msg", "重置密码成功");
        return map;
    }

    /**
     * 注册(包含上传店铺图片)
     * <p>
     * 提示：添加的店铺的图片必须大于320*180px，否则会添加失败；只支持jpg,jpeg,png,bmp格式图片；
     * 上传的图片所在路径为/shopImg/+邮箱号@前面的字符，如/shop/476879110；
     * 上传一张图片会生成两张图片，一张分辨率小的，一张分辨率大的，小的图片的名字为大的图片的名字加上min前缀
     *
     * @param req
     * @param email           邮箱
     * @param password        密码
     * @param confirmPassword 确认密码
     * @param code            邮箱验证码
     * @param shopName        店铺名
     * @param shopAddress     店铺地址
     * @param shopNotice      店铺公告
     * @return 根据是否注册成功返回相应的json提示信息
     */
    @RequestMapping(path = "/reg", method = RequestMethod.POST,
            params = {"email", "password", "confirmPassword", "code", "shopName", "shopAddress"})
    public Map<String, Object> register(HttpServletRequest req, String email, String password,
                                        String confirmPassword, String code, String shopName,
                                        String shopAddress, String shopNotice) {
        //用slf4J和log4J日志框架输出接收到的参数
        Logger logger = LoggerFactory.getLogger(ShopController.class);
        logger.info("email = " + email);
        logger.info("password = " + password);
        logger.info("confirmPassword = " + confirmPassword);
        logger.info("code = " + code);
        logger.info("shopName = " + shopName);
        logger.info("shopAddress = " + shopAddress);
        logger.info("shopNotice = " + shopNotice);

        Map<String, Object> map = new HashMap<String, Object>();

        //0.防止一些数据为""
        if ("".equals(email) || "".equals(password) || "".equals(shopName) || "".equals(shopAddress)) {
            map.put("success", false);
            map.put("msg", "请输入有效数据，请重新输入");
            return map;
        }

        //1.判断两次输入的密码是否相同
        if (!password.equals(confirmPassword)) {
            map.put("success", false);
            map.put("msg", "注册失败，两次密码不相同，请重新输入");
            return map;
        }

        //2.判断注册邮箱验证码是否正确
        //2.1 从session获取真正的邮箱验证码值
        /*HttpSession session = req.getSession();
        String realEmailCode = (String) session.getAttribute("emailCode");
        String realEmail = (String) session.getAttribute("email");
        session.removeAttribute("emailCode");
        session.removeAttribute("email");
        logger.info("registerSession = " + req.getSession());
        logger.info("realEmailCode = " + realEmailCode);
        logger.info("realEmail = " + realEmail);
        if (!code.equals(realEmailCode) && !email.equals(realEmail)) {
            map.put("success", false);
            map.put("msg", "注册失败，邮箱验证码输入错误");
            return map;
        }*/
        //2.1 从redis获取真正的邮箱验证码值
        String realEmailCode = stringRedisTemplate.opsForValue().get("regCode:" + email);
        stringRedisTemplate.delete("regCode:" + email);
        logger.info("realEmailCode = " + realEmailCode);
        if (!code.equals(realEmailCode)) {
            map.put("success", false);
            map.put("msg", "注册失败，邮箱验证码输入错误");
            return map;
        }

        //3.获取前端上传的文件
        MultipartFile multipartFile = UploadImgUtil.getFile(req, map);
        if (multipartFile == null) {
            return map;
        }

        //4.对前端上传的图片的后缀名进行限定
        Boolean b = UploadImgUtil.suffix(multipartFile, map);
        if (!b) {
            return map;
        }

        //5.将图片保存到硬盘
        //5.1获取图片存放的路径
        String basePath = "/shopImg/" + email.split("@")[0] + "/";
        //String imgPath = req.getSession().getServletContext().getRealPath(basePath);
        String imgPath = shopImgPath + email.split("@")[0] + "/";
        logger.info("imgPath=" + imgPath);
        File file = new File(imgPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        //5.2调用工具类将图片存入硬盘
        String imgName = UUID.randomUUID().toString().replace("-", "") + ".jpg";
        logger.info("imgName=" + imgName);
        try {
            Boolean bool = ImgUtil.reSize(multipartFile.getInputStream(), imgPath, imgName);
            if (!bool) {
                map.put("success", false);
                map.put("msg", "图片分辨率太低，添加失败");
                return map;
            }
        } catch (IOException e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("msg", "存入硬盘时异常，添加失败");
            return map;
        }

        //6.注册店铺
        boolean register = shopService.register(email, password, shopName, shopAddress, basePath + imgName, shopNotice);
        if (!register) {
            ImgUtil.deleteImage(imgPath, imgName);
            file.delete();
            map.put("success", false);
            map.put("msg", "注册失败，该邮箱已注册");
            return map;
        }

        //7.注册成功
        map.put("success", true);
        map.put("msg", "注册成功");
        return map;
    }

    /**
     * 修改店铺信息(包含修改店铺图片)
     *
     * @param req
     * @param shopName
     * @param shopAddress
     * @param shopNotice
     * @return
     */
    @RequestMapping(path = "/update", params = {"shopName", "shopAddress"})
    public Map<String, Object> updateShop(HttpServletRequest req, String shopName,
                                          String shopAddress, String shopNotice) {
        Logger logger = LoggerFactory.getLogger(ShopController.class);
        logger.info("shopName = " + shopName);
        logger.info("shopAddress = " + shopAddress);
        logger.info("shopNotice = " + shopNotice);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.防止一些数据为""
        if ("".equals(shopName) || "".equals(shopAddress)) {
            map.put("success", false);
            map.put("msg", "请输入有效数据，请重新输入");
            return map;
        }

        //2.从session中获取已经登录的shop对象
        Shop shopFromSession = (Shop) req.getSession().getAttribute("shop");
        logger.info("shopBySession = " + shopFromSession);

        //3.获取前端上传的文件
        String imgPath, imgName;
        imgPath = imgName = null;
        MultipartFile multipartFile = UploadImgUtil.getFile(req, map);
        if (multipartFile == null) {
            //multipartFile可以为null，为null代表没有修改菜品图片
            logger.info("修改店铺信息没有修改图片");
        } else {
            logger.info("修改店铺信息修改图片");
            //4.对前端上传的图片的后缀名进行限定
            Boolean b = UploadImgUtil.suffix(multipartFile, map);
            if (!b) {
                return map;
            }

            //5.查询店铺图片原来的名字，这样新图片就可以直接覆盖原图片了
            Shop shop = customerService.getShopInfoByShopId(shopFromSession.getShopId());
            imgName = shop.getShopImgPath().split("/")[3];

            //6.将图片保存到硬盘
            //6.1获取图片存放的路径
            /*imgPath = req.getSession().getServletContext().getRealPath("/")+"shopImg\\"
                    +shopFromSession.getShopEmail().split("@")[0]+"\\";*/
            imgPath = shopImgPath + shopFromSession.getShopEmail().split("@")[0] + "/";
            File file = new File(imgPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            //6.2调用工具类将图片存入硬盘
            try {
                Boolean bool = ImgUtil.reSize(multipartFile.getInputStream(), imgPath, imgName);
                if (!bool) {
                    map.put("success", false);
                    map.put("msg", "图片分辨率太低，修改失败");
                    return map;
                }
            } catch (IOException e) {
                e.printStackTrace();
                map.put("success", false);
                map.put("msg", "存入硬盘时异常，修改失败");
                return map;
            }
        }
        logger.info("imgPath=" + imgPath);
        logger.info("imgName=" + imgName);

        //7.将菜品信息保存到数据库
        Integer i = shopService.updateShop(shopName, shopAddress, shopNotice, shopFromSession.getShopId());

        //8.修改失败
        if (i != 1) {
            if (multipartFile != null) {
                ImgUtil.deleteImage(imgPath, imgName);
            }
            map.put("success", false);
            map.put("msg", "修改失败");
            return map;
        }

        //9.修改成功
        map.put("success", true);
        map.put("msg", "修改成功");
        return map;
    }

    /**
     * 修改店铺密码
     *
     * @param req
     * @param password
     * @param confirmPassword
     * @return
     */
    @RequestMapping(path = "/updatePassword", params = {"password", "confirmPassword"})
    public Map<String, Object> updatePassword(HttpServletRequest req, String password, String confirmPassword) {
        Logger logger = LoggerFactory.getLogger(ShopController.class);
        logger.info("password = " + password);
        logger.info("confirmPassword = " + confirmPassword);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.防止一些数据为""
        if ("".equals(password)) {
            map.put("success", false);
            map.put("msg", "请输入有效数据，请重新输入");
            return map;
        }

        //2.判断两次输入的密码是否相同
        if (!password.equals(confirmPassword)) {
            map.put("success", false);
            map.put("msg", "修改失败，两次密码不相同，请重新输入");
            return map;
        }

        //3.从session中获取已经登录的shop对象
        Shop shopFromSession = (Shop) req.getSession().getAttribute("shop");
        logger.info("shopBySession = " + shopFromSession);

        //4.向数据库修改密码
        Integer integer = shopService.updateShopPassword(shopFromSession.getShopId(), password);

        //5.修改失败
        if (integer != 1) {
            map.put("success", false);
            map.put("msg", "修改失败");
            return map;
        }

        //6.修改成功
        map.put("success", true);
        map.put("msg", "修改成功");
        return map;
    }

    /**
     * 获取店铺信息
     *
     * @param session
     * @return
     */
    @RequestMapping("/getShopInfo")
    public Map<String, Object> getShopInfo(HttpSession session) {

        //用slf4J和log4J日志框架输出接收到的参数
        Logger logger = LoggerFactory.getLogger(ShopController.class);
        logger.info("getShopInfoSession = " + session);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.从session中获取已经登录的shop对象
        Shop shopFromSession = (Shop) session.getAttribute("shop");
        logger.info("shopBySession = " + shopFromSession);
        if (shopFromSession == null) {
            map.put("success", false);
            map.put("msg", "请先登录");
            return map;
        }

        //2.返回店铺信息
        map.put("success", true);
        map.put("shop", shopFromSession);
        return map;
    }

    /**
     * 退出登录
     *
     * @param session
     * @param resp
     * @return
     */
    @RequestMapping("/loginOut")
    public Map<String, Object> loginOut(HttpSession session, HttpServletResponse resp) {
        //用slf4J和log4J日志框架输出接收到的参数
        Logger logger = LoggerFactory.getLogger(ShopController.class);
        logger.info("loginOutSession = " + session);

        Map<String, Object> map = new HashMap<String, Object>();
        //1.从session删除shop对象
        session.removeAttribute("shop");
        //2.从cookie中删除shop信息
        Cookie cookie = new Cookie("shop", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        resp.addCookie(cookie);

        map.put("success", true);
        map.put("msg", "已退出登录");
        return map;
    }

    /**
     * 根据桌号生成二维码
     *
     * @param resp
     * @param deskId 桌子编号
     * @return
     */
    @RequestMapping(value = "/getQRCode", params = "deskId")
    public Map<String, Object> getQRCode(HttpServletRequest req, HttpServletResponse resp, Integer deskId) {

        Logger logger = LoggerFactory.getLogger(ShopController.class);
        logger.info("deskId = " + deskId);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.从session中获取已经登录的shop对象
        Shop shopFromSession = (Shop) req.getSession().getAttribute("shop");
        logger.info("shopBySession = " + shopFromSession);

        //2.生成https://open.weixin.qq.com/sns/getexpappinfo?appid=wx3e78be1650ba47cc&path=pages/start/start.html?shopId=1000&deskId=5#wechat-redirect格式的url
        String url = QRCPrefix + "shopId=" +
                shopFromSession.getShopId() + "&deskId=" + deskId + QRCSuffix;
        logger.info("url = " + url);

        //3.获取logo的真实路径
        //String logoPath = req.getServletContext().getRealPath("/public/logo/logo.png");
        ApplicationHome home = new ApplicationHome(getClass());
        File jarFile = home.getSource();
        String logoPath = jarFile.getPath().replace("\\", "/") + "/public/logo/logo.png";

        //InputStream in = getClass().getClassLoader().getResourceAsStream("/public/logo/logo.png");
        logger.info("logoPath=" + logoPath);

        try {
            //4.生成二维码
            BufferedImage bufferedImage = QRCodeUtils.encodeMy(url, logoPath, false);
            //5.通过输出流将二维码显示到前端
            ImageIO.write(bufferedImage, "jpg", resp.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("msg", "生成二维码失败");
            return map;
        }

        map.put("success", true);
        map.put("msg", "生成二维码成功");
        return map;
    }
}
