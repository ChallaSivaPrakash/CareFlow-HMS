package com.careflow.hms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CareflowApplication {
    public static void main(String[] args) {
        SpringApplication.run(CareflowApplication.class, args);
    }
}