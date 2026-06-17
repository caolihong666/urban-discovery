package com.urbandiscovery;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.urbandiscovery.mapper")
@SpringBootApplication
public class UrbanDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrbanDiscoveryApplication.class, args);
    }

}
