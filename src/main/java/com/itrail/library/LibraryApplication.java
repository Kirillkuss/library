package com.itrail.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;
/**
 * http://localhost:8094/swagger-ui/index.html
 *  http://localhost:8094/library/index.html
 */
@Slf4j
@SpringBootApplication
public class LibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
		log.info( "Library start!");
	}

	
}
