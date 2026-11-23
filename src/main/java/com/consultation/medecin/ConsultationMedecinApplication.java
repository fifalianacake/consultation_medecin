package com.consultation.medecin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ConsultationMedecinApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsultationMedecinApplication.class, args);
    }
}
