package com.jsdx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//开启定时任务支持
@EnableScheduling
public class GCUSeatingApplication {

    public static void main(String[] args) {
        SpringApplication.run(GCUSeatingApplication.class, args);
    }

}
