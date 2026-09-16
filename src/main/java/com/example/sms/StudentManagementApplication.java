package com.example.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point of the whole application - this is the class with
 * the main() method that actually starts everything up.
 *
 * @SpringBootApplication is really three annotations combined into
 * one:
 *   - @Configuration: this class can define Spring beans
 *   - @EnableAutoConfiguration: Spring Boot automatically configures
 *     things like the embedded web server and the database connection
 *     based on what's on the classpath and in application.properties
 *   - @ComponentScan: Spring automatically finds and registers all of
 *     our @Service, @RestController, @Repository, and @Configuration
 *     classes anywhere in the com.example.sms package (and its
 *     sub-packages), without us having to list them out one by one
 */
@SpringBootApplication
public class StudentManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentManagementApplication.class, args);
    }
}
