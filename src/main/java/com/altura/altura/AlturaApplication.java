package com.altura.altura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.altura.altura")
public class AlturaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlturaApplication.class, args);
	}

}
