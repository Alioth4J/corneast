package com.alioth4j.corneast_core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for Corneast Core.
 *
 * @author Alioth Null
 */
@SpringBootApplication
@EnableAsync
public class CorneastCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(CorneastCoreApplication.class, args);
	}

}
