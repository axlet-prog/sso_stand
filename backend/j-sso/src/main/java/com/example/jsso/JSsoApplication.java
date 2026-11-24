package com.example.jsso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JSsoApplication {

	public static void main(String[] args) {
		System.out.println("CURRENT WORKING DIRECTORY: " + System.getProperty("user.dir"));
		SpringApplication.run(JSsoApplication.class, args);
	}

}
