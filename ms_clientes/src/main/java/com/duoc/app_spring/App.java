package com.duoc.app_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class App {
    public static void main(String[] args) {
        System.out.println("Iniciando aplicación...");
        SpringApplication.run(App.class, args);
        System.out.println("Aplicación iniciada correctamente en el puerto 8082");
        System.out.println("Nombre de la aplicación: " + SpringApplication.class.getSimpleName());
    }
}