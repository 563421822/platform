package com.example.accost;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(scanBasePackages = "com.example")
@MapperScan(value = "com.example.mapper")
@EnableEurekaClient
public class AccostApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccostApplication.class, args);
    }

}
