package dev.michaelfarrant.simplestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync
public class SimplestoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimplestoreApplication.class, args);
    }

}
