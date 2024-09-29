package com.example.smartplant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@EnableAsync
@SpringBootApplication
@EnableWebSocketMessageBroker
public class SmartplantApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartplantApplication.class, args);
    }

}
