package com.jisen.seckill;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class SeckillOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeckillOrderApplication.class, args);
    }

}
