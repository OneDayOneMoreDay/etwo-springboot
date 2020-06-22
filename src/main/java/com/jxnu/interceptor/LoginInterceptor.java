package com.jxnu.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxnu.domain.Shop;
import org.omg.PortableInterceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;

/**
 * @date 2020/4/17 13:53
 */
public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("{}经过了LoginInterceptor拦截器",request.getRequestURI());

        boolean bool = false;
        //1.判断session是否存在shop对象（即判断是否已经登录了）
        HttpSession session = request.getSession();
        Shop shop = (Shop) session.getAttribute("shop");

        if (shop!=null){
            bool = true;
        }else {
        //2.判断cookie中是否存在shop信息
            Cookie[] cookies = request.getCookies();
            if(cookies != null && cookies.length > 0){
                for (Cookie cookie : cookies) {
                    String cookieName = cookie.getName();
                    //2.1判断cookie中是否存在名为shop的cookie
                    if ("shop".equals(cookieName)) {
                        String str_shop = cookie.getValue();
                        //System.out.println("str_shop解码前="+str_shop);
                        //2.2将cookie进行解码
                        str_shop = URLDecoder.decode(str_shop,"utf-8");
                        //System.out.println("str_shop解码后="+str_shop);
                        ObjectMapper om = new ObjectMapper();
                        //2.3将json字符串转化为shop对象
                        shop = om.readValue(str_shop, Shop.class);
                        //System.out.println("shop对象+"+shop);
                        //2.4向session中添加shop对象
                        session.setAttribute("shop",shop);
                        bool = true;
                    }
                }
            }
        }
        if(!bool){
//            response.sendRedirect(request.getContextPath()+"/html?action=login");
            logger.info("LoginInterceptor拦截器拦截了{}",request.getRequestURI());
            response.getWriter().write("please login");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
