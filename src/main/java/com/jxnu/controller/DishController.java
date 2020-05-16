package com.jxnu.controller;

import com.jxnu.domain.Dish;
import com.jxnu.domain.Shop;
import com.jxnu.service.DishService;
import com.jxnu.utils.ImgUtil;
import com.jxnu.utils.UploadImgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @date 2020/4/17 21:16
 *
 * 添加一个菜品（包含上传菜品图片）
 * 根据菜品分类id查询菜品信息
 * 删除一个菜品
 * 修改一个菜品（包含菜品图片）
 */
@Controller
@ResponseBody
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Value("${dishImgPath}")
    private String dishImgPath;

    private Logger logger = LoggerFactory.getLogger(DishController.class);

    /**
     * 添加一个菜品（包含上传菜品图片）
     * 提示：添加的菜品的图片必须大于320*180px，否则会添加失败；只支持jpg,jpeg,png,bmp格式图片；
     * 上传的图片所在路径为/dishImg/+shopId；
     * 上传一张图片会生成两张图片，一张分辨率小的，一张分辨率大的，小的图片的名字为大的图片的名字加上min前缀
     *
     * @param request
     * @param dishTypeId 添加的菜品的菜品分类id
     * @param dishName   添加的菜品的名字
     * @param dishPrice  添加的菜品的价格，必须大于0
     * @param dishNumber 添加的菜品的数量，必须大于0
     * @return
     */
    @RequestMapping(path = "/add", method = RequestMethod.POST,
            params = {"dishTypeId", "dishName", "dishPrice", "dishNumber"})
    public Map<String, Object> addDish(HttpServletRequest request, Integer dishTypeId, String dishName,
                                       Float dishPrice, Integer dishNumber) {

        logger.info("dishTypeId=" + dishTypeId);
        logger.info("dishName=" + dishName);
        logger.info("dishPrice=" + dishPrice);
        logger.info("dishNumber=" + dishNumber);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.从session中获取已经登录的shop对象
        Shop shopFromSession = (Shop) request.getSession().getAttribute("shop");
        logger.info("shopFromSession = " + shopFromSession);
        if (shopFromSession == null) {
            map.put("success", false);
            map.put("msg", "请先登录");
            return map;
        }

        //2.防止菜品名为""，负值价格和数量<0
        if ("" == dishName.trim() || dishNumber < 0 || dishPrice < 0) {
            map.put("success", false);
            map.put("msg", "请输入有效数据");
            return map;
        }

        //3.获取前端上传的文件
        MultipartFile multipartFile = UploadImgUtil.getFile(request, map);
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
        String basePath = "/dishImg/" + shopFromSession.getShopId()+"/";
        //String imgPath = request.getSession().getServletContext().getRealPath(basePath);
        String imgPath = dishImgPath + shopFromSession.getShopId()+"/";
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

        //6.将菜品信息保存到数据ku
        Integer i = dishService.insertDish(dishTypeId, dishName, basePath + imgName, dishPrice,
                dishNumber, shopFromSession.getShopId());
        if (i == 2) {
            ImgUtil.deleteImage(imgPath, imgName);
            map.put("success", false);
            map.put("msg", "此店铺不存在该dishTypeId，添加失败");
            return map;
        }
        if (i == 3) {
            ImgUtil.deleteImage(imgPath, imgName);
            map.put("success", false);
            map.put("msg", "店铺已经存在同名的菜品，添加失败");
            return map;
        }
        if (i != 3 && i != 2 && i != 1) {
            ImgUtil.deleteImage(imgPath, imgName);
            map.put("success", false);
            map.put("msg", "添加失败");
            return map;
        }

        map.put("success", true);
        map.put("msg", "添加菜品成功");
        return map;
    }

    /**
     * 根据菜品分类id查询菜品信息
     * @param session
     * @return
     */
    @RequestMapping(path = "getDishByDishTypeId",params = "dishTypeId")
    public Map<String, Object> getDishByDishTypeId(HttpSession session,Integer dishTypeId){
        logger.info("dishTypeId="+dishTypeId);
        Map<String, Object> map = new HashMap<String, Object>();

        //1.从session中获取已经登录的shop对象
        Shop shopFromSession = (Shop) session.getAttribute("shop");
        logger.info("shopFromSession = " + shopFromSession);
        if (shopFromSession == null) {
            map.put("success", false);
            map.put("msg", "请先登录");
            return map;
        }

        //2.从数据库获取菜品信息
        List<Dish> dishList = dishService.findDishByDishTypeId(dishTypeId);
        if (dishList==null||dishList.size()==0){
            map.put("success", false);
            map.put("msg", "获取菜品信息失败");
            return map;
        }

        //3.返回结果
        map.put("success", true);
        map.put("dishList", dishList);
        return map;
    }

    /**
     * 删除一个菜品
     * @param session
     * @param dishId
     * @return
     */
    @RequestMapping(path = "delete",params = "dishId")
    public Map<String, Object> deleteDish(HttpSession session,Integer dishId){
        logger.info("dishId="+dishId);
        Map<String, Object> map = new HashMap<String, Object>();

        //1.从session中获取已经登录的shop对象
        Shop shopFromSession = (Shop) session.getAttribute("shop");
        logger.info("shopFromSession = " + shopFromSession);
        if (shopFromSession == null) {
            map.put("success", false);
            map.put("msg", "请先登录");
            return map;
        }

        //2.从数据库删除菜品
        Integer i = dishService.deleteDish(dishId, shopFromSession.getShopId());
        if (i==2){
            map.put("success", false);
            map.put("msg", "此dishId不属于此店铺，删除失败");
            return map;
        }
        if (i!=2&&i!=1){
            map.put("success", false);
            map.put("msg", "删除失败");
            return map;
        }

        //3.返回结果
        map.put("success", true);
        map.put("msg", "删除成功");
        return map;
    }

    /**
     * 修改一个菜品（包含菜品图片）
     * 提示：修改菜品时是否上传图片是可选的；添加的菜品的图片必须大于320*180px，否则会添加失败；
     * 只支持jpg,jpeg,png,bmp格式图片；修改菜品时修改了图片，图片的url也不会改变，新的图片会覆盖原来的图片；
     * 上传一张图片会生成两张图片，一张分辨率小的，一张分辨率大的，小的图片的名字为大的图片的名字加上min前缀
     *
     * @param request
     * @param dishId     修改的菜品的id
     * @param dishTypeId 修改的菜品的菜品分类id
     * @param dishName   修改的菜品的名字
     * @param dishPrice  修改的菜品的价格，必须大于0
     * @param dishNumber 修改的菜品的数量，必须大于0
     * @return
     */
    @RequestMapping(path = "/update", method = RequestMethod.POST,
            params = {"dishId","dishTypeId", "dishName", "dishPrice", "dishNumber"})
    public Map<String, Object> updateDish(HttpServletRequest request,Integer dishId, Integer dishTypeId, String dishName,
                                       Float dishPrice, Integer dishNumber) {

        logger.info("dishId=" + dishId);
        logger.info("dishTypeId=" + dishTypeId);
        logger.info("dishName=" + dishName);
        logger.info("dishPrice=" + dishPrice);
        logger.info("dishNumber=" + dishNumber);

        Map<String, Object> map = new HashMap<String, Object>();

        //1.从session中获取已经登录的shop对象
        Shop shopFromSession = (Shop) request.getSession().getAttribute("shop");
        logger.info("shopFromSession = " + shopFromSession);

        //2.防止菜品名为""，赋值价格和数量<0
        if ("" == dishName.trim() || dishNumber < 0 || dishPrice < 0) {
            map.put("success", false);
            map.put("msg", "请输入有效数据");
            return map;
        }

        //3.获取前端上传的文件
        String imgPath,imgName;
        imgPath = imgName = null;
        MultipartFile multipartFile = UploadImgUtil.getFile(request, map);
        if (multipartFile == null) {
            //multipartFile可以为null，为null代表没有修改菜品图片
            logger.info("修改菜品没有修改图片");
        }else {
            logger.info("修改菜品修改图片");
            //4.对前端上传的图片的后缀名进行限定
            Boolean b = UploadImgUtil.suffix(multipartFile, map);
            if (!b) {
                return map;
            }

            //5.查询菜品图片原来的名字，这样新图片就可以直接覆盖原图片了
            Dish dish = dishService.findDishByDishId(dishId);
            imgName = dish.getDishImgPath().split("/")[3];

            //6.将图片保存到硬盘
            //6.1获取图片存放的路径
            //imgPath = request.getSession().getServletContext().getRealPath("/")+"dishImg\\"+shopFromSession.getShopId()+"\\";
            imgPath = dishImgPath+shopFromSession.getShopId()+"/";
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
        logger.info("imgPath="+imgPath);
        logger.info("imgName="+imgName);

        //7.将菜品信息保存到数据库
        Integer i = dishService.updateDish(dishId,dishTypeId, dishName, dishPrice,
                dishNumber, shopFromSession.getShopId());
        if (i == 2) {
            if (multipartFile!=null){
                ImgUtil.deleteImage(imgPath, imgName);
            }
            map.put("success", false);
            map.put("msg", "此店铺不存在该dishId，修改失败");
            return map;
        }
        if (i == 3) {
            if (multipartFile!=null){
                ImgUtil.deleteImage(imgPath, imgName);
            }
            map.put("success", false);
            map.put("msg", "此店铺不存在该dishTypeId，修改失败");
            return map;
        }
        if (i == 4) {
            if (multipartFile!=null){
                ImgUtil.deleteImage(imgPath, imgName);
            }
            map.put("success", false);
            map.put("msg", "店铺已经存在同名的菜品，修改失败");
            return map;
        }
        if (i != 4 && i != 3 && i != 2 && i != 1) {
            if (multipartFile!=null){
                ImgUtil.deleteImage(imgPath, imgName);
            }
            map.put("success", false);
            map.put("msg", "修改失败");
            return map;
        }

        map.put("success", true);
        map.put("msg", "修改菜品成功");
        return map;
    }
}
