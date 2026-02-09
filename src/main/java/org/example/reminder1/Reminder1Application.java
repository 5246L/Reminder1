package org.example.reminder1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class Reminder1Application {

    public static void main(String[] args) {
        SpringApplication.run(Reminder1Application.class, args);
    }

}
