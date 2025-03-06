package com.alioth4j.corneast_eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class CorneastEurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CorneastEurekaApplication.class, args);
	}

}
