package com.jxnu.config;

import com.jxnu.interceptor.LoginInterceptor;
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

    /**
     * 解决跨域问题
     * @return
     */
    @Bean
    public WebMvcConfigurer corsConfigurer()
    {
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
