package com.jxnu.utils;

import com.jxnu.controller.DishController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @date 2020/4/19 20:57
 */
public class UploadImgUtil {

    //从配置文件读取上传图片允许的后缀名
    private static List<String> suffixList = Arrays.asList("jpg","jpeg","png","bmp");

//    static {
//        Properties properties = new Properties();
//        InputStream in = DishController.class.getClassLoader().getResourceAsStream("fileSuffix.properties");
//        try {
//            properties.load(in);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String suffix = properties.getProperty("suffix");
//        String[] suffixs = suffix.split(",");
//        suffixList = Arrays.asList(suffixs);
//    }

    private static Logger logger = LoggerFactory.getLogger(UploadImgUtil.class);

    /**
     * 获取前端上传的文件
     * @param request
     * @param map Map<String, Object> map = new HashMap<String, Object>();
     * @return 获取前端上传的文件成功返回MultipartFile接口的实现类，失败返回null
     */
    public static MultipartFile getFile(HttpServletRequest request, Map<String,Object> map){
        MultipartFile multipartFile = null;
        if (request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            //3.1 获取上传的文件map集合,集合的key为input的name属性名，value为文件对应的MultipartFile
            Map<String, MultipartFile> fileMap = multipartHttpServletRequest.getFileMap();
            //3.2 若前端没有上传文件，返回提示信息
            if (fileMap.isEmpty() || fileMap.size() == 0) {
                map.put("success", false);
                map.put("msg", "请选择图片~");

                return null;
            }
            //3.3 遍历fileMap
            for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
                logger.info("上传的文件：" + entry.getKey() + "=" + entry.getValue().getOriginalFilename());
                multipartFile = entry.getValue();
                break;
            }
        } else {

            map.put("success", false);
            map.put("msg", "请选择图片");
            return null;
        }
        return multipartFile;
    }

    /**
     * 对前端上传的图片的后缀名进行限定
     * @param multipartFile
     * @param map Map<String, Object> map = new HashMap<String, Object>();
     * @return true 成功；false 失败
     */
    public static Boolean suffix(MultipartFile multipartFile,Map<String,Object> map){
        String[] strings = multipartFile.getOriginalFilename().split("\\.");
        if (strings.length == 0) {
            map.put("success", false);
            map.put("msg", "文件格式不支持,添加菜品失败~");
            return false;
        }
        String end = strings[strings.length - 1].toLowerCase();//上传文件的后缀名
        logger.info("end=" + end);
        logger.info("suffixList=" + suffixList);
        if (!suffixList.contains(end)) {
            map.put("success", false);
            map.put("msg", "文件格式不支持,添加菜品失败");
            return false;
        }
        return true;
    }
}
