package com.jxnu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.jxnu.mapper")
@EnableCaching //开启缓存
public class EtwoSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtwoSpringbootApplication.class, args);
    }

}
