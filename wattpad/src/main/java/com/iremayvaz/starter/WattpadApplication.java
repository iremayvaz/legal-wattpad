package com.iremayvaz.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.iremayvaz"})    // Database repository erişimi
@EntityScan(basePackages = {"com.iremayvaz"})               // Database tablolarını oluşturmak için
@ComponentScan(basePackages = {"com.iremayvaz"})

public class WattpadApplication {

    public static void main(String[] args) {
        SpringApplication.run(WattpadApplication.class, args);
    }

}
