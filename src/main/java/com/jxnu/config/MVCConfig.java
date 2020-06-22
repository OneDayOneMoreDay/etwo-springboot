package com.jxnu.config;

import com.jxnu.interceptor.LoginInterceptor;
import com.jxnu.interceptor.ShowInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
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
        // 请求/html/index就会转发到classpath:/static/html/index.html
        registry.addViewController("/html/index").setViewName("index");
        registry.addViewController("/html/login").setViewName("login");
        registry.addViewController("/html/register").setViewName("register");
    }

    /**
     * 注册拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new ShowInterceptor()).addPathPatterns("/**");

        //SpringMVC下，拦截器的注册需要排除对静态资源的拦截(*.css,*.js)
        //SpringBoot已经做好了静态资源的映射，因此我们无需任何操作
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/",
                        "/index",
                        "/index.html",
                        "/**/404.html",
                        "/**/ForgetPassword.html",
                        "/**/*.jpg",
                        "/**/*.png",
                        "/**/*.gif",
                        "/**/*.css",
                        "/**/*.js",
                        "/**/*.ico",
                        "/**/*.txt",
                        "/layui/**",

                        "/html/index",
                        "/html/login",
                        "/html/register",
                        "/error/**",
                        "/cus/**",
                        "/mnp/**",
                        "/test**",
                        "/Kaptcha",
                        "/waiter/login",
                        "/shop/login",
                        "/shop/reg",
                        "/shop/loginOut",
                        "/shop/getEmailCode",
                        "/shop/getForgetPasswordEmailCode",
                        "/shop/resetPassword",
                        "/shop/getEmailCode"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        /*registry.addResourceHandler("/public/**").addResourceLocations("classpath:/public/");*/
        registry.addResourceHandler("/shopImg/**").addResourceLocations("file:"+shopImgPath);
        registry.addResourceHandler("/dishImg/**").addResourceLocations("file:"+dishImgPath);
    }

    /**
     * 解决跨域问题
     * @return
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").
                        allowedOrigins("*"). //允许跨域的域名，可以用*表示允许任何域名使用
                        allowedMethods("*"). //允许任何方法（post、get等）
                        allowedHeaders("*"). //允许任何请求头
                        allowCredentials(true). //带上cookie信息
                        exposedHeaders(HttpHeaders.SET_COOKIE).maxAge(3600L); //maxAge(3600)表明在3600秒内，不需要再发送预检验请求，可以缓存该结果
            }
        };
    }
}
