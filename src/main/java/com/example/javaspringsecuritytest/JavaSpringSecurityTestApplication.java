package com.example.javaspringsecuritytest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class JavaSpringSecurityTestApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(JavaSpringSecurityTestApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(JavaSpringSecurityTestApplication.class);
	}
}