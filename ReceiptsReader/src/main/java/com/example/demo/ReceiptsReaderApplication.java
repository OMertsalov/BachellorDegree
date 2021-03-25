package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import nu.pattern.OpenCV;

@SpringBootApplication
public class ReceiptsReaderApplication {

	public static void main(String[] args) {
		OpenCV.loadLocally();
		SpringApplication.run(ReceiptsReaderApplication.class, args);
	}

}
