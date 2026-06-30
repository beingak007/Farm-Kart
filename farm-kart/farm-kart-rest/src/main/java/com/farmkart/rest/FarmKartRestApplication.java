package com.farmkart.rest;

import com.farmkart.rest.configuration.FarmKartAppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(FarmKartAppConfig.class)
public class FarmKartRestApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(FarmKartRestApplication.class, args);
    }
}
