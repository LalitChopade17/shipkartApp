package com.shipkart.Shipkart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages ="com.shipkart")
public class ShipkartApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShipkartApplication.class, args);
	}

}
