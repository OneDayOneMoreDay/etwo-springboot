package com.jxnu.zmytest;

import com.jxnu.domain.Shop;
import com.jxnu.utils.QRCodeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @date 2020/4/15 21:55
 * 测试用的，跟业务逻辑没关系
 */
@Controller
@ResponseBody
//@SessionAttributes(value = "a")
public class TestController {

    @RequestMapping("/test1")
    public String test1(ModelMap modelMap){
        modelMap.addAttribute("a","b");
        return "test1";
    }

    @RequestMapping("/test2")
    public String test2(ModelMap modelMap){
        String string = (String) modelMap.get("a");
        System.out.println(string);
        return "test2";
    }

    @RequestMapping("/test3")
    public String test3(Model model, HttpSession session){
        System.out.println(session);
        session.setAttribute("test3key","test3value");
//        model.addAttribute("a","a");
        return "test3";
    }

    @RequestMapping("/test4")
    public String test4(ModelMap modelMap){
//        String string = (String) modelMap.get("a");
        String string = (String) modelMap.get("test3key");
        System.out.println(string);
        return "test4";
    }

    @RequestMapping("/test5")
    public String test5(HttpServletRequest req){
        String string1 = (String) req.getSession().getAttribute("test3key");
        String string2 = (String) req.getSession().getAttribute("a");
        System.out.println("string1="+string1);
        System.out.println("string2="+string2);
        return "string";
    }

    @RequestMapping("/test6")
    public String test6(HttpServletRequest req){
        System.out.println("test6...");
        req.getSession();

        return "test6";
    }

    @RequestMapping("/test7")
    public String test7(HttpServletRequest req, HttpServletResponse response){
        System.out.println("test7...");
        Cookie c= new Cookie("mag", "a");
        response.addCookie(c);

        return "test7";
    }

    @RequestMapping("/test8")
    public String test8(HttpServletRequest req, HttpServletResponse response){
        System.out.println("test8...");
        return "test8";
    }

    @RequestMapping("/test9")
    public String test9(HttpServletRequest req, HttpServletResponse response){
        System.out.println("test...");
        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            System.out.print(cookie.getName()+"=");
            System.out.println(cookie.getValue());
        }
        return "test9";
    }

    @RequestMapping(path = "/test10")
    public String test10(HttpSession session,HttpServletRequest request,MultipartFile upload){

        System.out.println("test10");
        System.out.println(upload.isEmpty());

        // 上传的位置
        String path = session.getServletContext().getRealPath("/");
        System.out.println(path);
        Shop shop = (Shop) session.getAttribute("shop");
        path = path + 1000;
        // 判断，该路径是否存在
        File file = new File(path);
        if(!file.exists()){
            // 创建该文件夹
            file.mkdirs();
        }

        String filename = upload.getOriginalFilename();
        // 把文件的名称设置唯一值，uuid
        String uuid = UUID.randomUUID().toString().replace("-", "");
        filename = uuid+"_"+filename;
        // 完成文件上传
        try {
            upload.transferTo(new File(path,filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "test10";
    }

    @RequestMapping(path = "/test11")
    public String test11(HttpServletRequest request,String name1,String name2){
        System.out.println("test11");
//        String basePath = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
//        System.out.println("basePath="+basePath);
        if (request instanceof MultipartHttpServletRequest) {
            System.out.println("有文件");
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;

            // 获取上传的文件
            Map<String, MultipartFile> fileMap = multipartHttpServletRequest.getFileMap();

            System.out.println("fileMap.isEmpty()="+fileMap.isEmpty());
            System.out.println("fileMap.size()="+fileMap.size());

            for(Map.Entry<String, MultipartFile> entry : fileMap.entrySet()){

                // 对文件进处理
                System.out.println(entry.getKey() + ":" + entry.getValue().getOriginalFilename());
            }
        }
        // 对name进行处理
        System.out.println("name1="+name1);
        System.out.println("name2="+name2);

        return "test11";
    }

    @RequestMapping(path = "/test12")//
    public String test12(Integer shopId,@RequestParam("list[]") List<Integer> list){
        System.out.println("test11");
        System.out.println("shopId="+shopId);
        System.out.println("list="+list);

        return "test12";
    }

    @RequestMapping(path = "/test13",params = {"shopId"})
    public String test13(Integer shopId,@RequestParam Map<String,String> map){
        System.out.println("shopId="+shopId);
        System.out.println("map="+map);
        String s = map.remove("shopId");
        System.out.println("s="+s);
        System.out.println(map);
        for (String s1 : map.keySet()) {
            System.out.print(s1);
            System.out.println(map.get(s1));
        }
        return "test13";
    }

    @RequestMapping(path = "/test14")
    public String test14(Integer deskNum,HttpServletResponse response) throws Exception {
        System.out.println("test14...");
        System.out.println("deskNum="+deskNum);

        BufferedImage bufferedImage = QRCodeUtils.encodeMy("123456",
                "C:\\Users\\dk\\Desktop\\aa\\logo.png", true);

        OutputStream outputStream = new FileOutputStream(new File("C:/Users/dk/Desktop/aa/qrc.jpg"));
        ImageIO.write(bufferedImage,"jpg",response.getOutputStream());
        ImageIO.write(bufferedImage,"jpg",response.getOutputStream());
//        ImageIO.write(bufferedImage,"jgp",outputStream);
        outputStream.close();
        return "test14";
    }

    @RequestMapping(path = "/test15")
    public String test15() {
        return "a";
    }
}
