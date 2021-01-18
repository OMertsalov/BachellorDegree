package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import nu.pattern.OpenCV;

@SpringBootApplication
public class OpenCvApplication {

	public static void main(String[] args) {
		OpenCV.loadShared();
		SpringApplication.run(OpenCvApplication.class, args);
	}

}
