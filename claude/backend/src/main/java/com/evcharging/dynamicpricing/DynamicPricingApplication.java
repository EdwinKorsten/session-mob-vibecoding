package com.evcharging.dynamicpricing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DynamicPricingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicPricingApplication.class, args);
    }

}