package com.jisen.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.jisen.seckill.mapper")
public class SeckillUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeckillUserApplication.class, args);
    }

}
