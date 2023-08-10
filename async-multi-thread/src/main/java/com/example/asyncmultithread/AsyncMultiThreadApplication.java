package com.example.asyncmultithread;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AsyncMultiThreadApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsyncMultiThreadApplication.class, args);
    }

}

