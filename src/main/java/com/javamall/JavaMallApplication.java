package com.javamall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.javamall.mapper")
public class JavaMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaMallApplication.class, args);
    }

}
