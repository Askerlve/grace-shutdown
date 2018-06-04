package com.askerlve.grace.shutdown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@ComponentScan("com.askerlve.grace")
public class GraceShutdownTestApplication {

    @GetMapping("/hello")
    public String hello() {
        System.out.println("req.........");
        try {
            Thread.sleep(1000 * 60 * 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello";
    }

    public static void main(String[] args) {
        SpringApplication.run(GraceShutdownTestApplication.class, args);
    }
}
