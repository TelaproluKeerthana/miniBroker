package com.example.tinybroker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TinyBrokerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TinyBrokerApplication.class, args);
    }
}