package com.homework.estate_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class EstateProjectApplication {

    public static void main(String[] args) {

        SpringApplication.run(EstateProjectApplication.class, args);

        // ApplicationContext ctx = new AnnotationConfigApplicationContext("com.homework.estate_project");
    }

}
