package com.aitu.sdp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Application entry point demonstrating Builder, Factory Method, and Abstract Factory. */
@SpringBootApplication(scanBasePackages = "com.aitu.sdp")
public class Main {
    public Main() {
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
