package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 主启动类
 * <p>
 * 功能说明：
 * 1. @SpringBootApplication: 组合注解，包含自动配置、组件扫描等功能
 * 2. @MapperScan: 扫描MyBatis Mapper接口所在的包
 * </p>
 */
@SpringBootApplication
@MapperScan("com.example.demo.mapper")
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        System.out.println("========================================");
        System.out.println("    后端服务启动成功！");
        System.out.println("    端口：8080");
        System.out.println("    接口文档：http://localhost:8080/doc.html");
        System.out.println("========================================");
    }
}