package com.evcharging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EvChargingApplication {
    public static void main(String[] args) {
        SpringApplication.run(EvChargingApplication.class, args);
    }
}