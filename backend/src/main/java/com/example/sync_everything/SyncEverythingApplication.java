package com.example.sync_everything;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.sync_everything.dao")
public class SyncEverythingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SyncEverythingApplication.class, args);
    }

}
