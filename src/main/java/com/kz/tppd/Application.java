package com.kz.tppd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * springboot启动类
 * Created by kz on 2022/1/4.
 */
@SpringBootApplication(scanBasePackages = "com.kz.tppd")
public class Application{

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
