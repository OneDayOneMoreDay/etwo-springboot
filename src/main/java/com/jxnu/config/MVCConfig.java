package com.jxnu.config;

import com.jxnu.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * 扩展SpringMVC配置
 */
//@EnableWebMvc 加了该注解表示全面接管配置springmvc，springboot对mvc的自动配置就会生效
@Configuration
public class MVCConfig implements WebMvcConfigurer {

    @Value("${shopImgPath}")
    private String shopImgPath;
    @Value("${dishImgPath}")
    private String dishImgPath;
    /**
     * 对静态资源的处理
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/index").setViewName("index");
    }

    /**
     * 注册拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //super.addInterceptors(registry);
        //静态资源；  *.css , *.js
        //SpringBoot已经做好了静态资源映射
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/",
                        "/**/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/**/*.ico",
                        "/**/*.txt",
                        "/Kaptcha",
                        "static/**",
                        "/waiter/login",
                        "/shop/login",
                        "/shop/reg",
                        "/shop/loginOut",
                        "/shop/getEmailCode",
                        "/cus/**",
                        "/mnp/**",
                        "/shop/getEmailCode",
                        "/test**"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/public/**").addResourceLocations("classpath:/public/");*/
        registry.addResourceHandler("/shopImg/**").addResourceLocations("file:"+shopImgPath);
        registry.addResourceHandler("/dishImg/**").addResourceLocations("file:"+dishImgPath);
    }
}
