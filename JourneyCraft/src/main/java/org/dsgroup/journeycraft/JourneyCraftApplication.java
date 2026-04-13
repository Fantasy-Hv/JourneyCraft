package org.dsgroup.journeycraft;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.dsgroup.journeycraft")
public class JourneyCraftApplication {

    public static void main(String[] args) {
        SpringApplication.run(JourneyCraftApplication.class, args);
    }

}
