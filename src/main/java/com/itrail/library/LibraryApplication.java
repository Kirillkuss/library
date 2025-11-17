package com.itrail.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import lombok.extern.slf4j.Slf4j;
/**
 * http://localhost:8094/library/swagger-ui/index.html
 * http://localhost:8094/library/app/index.html
 */
@Slf4j
@SpringBootApplication
public class LibraryApplication  {

	public static void main(String[] args) {
		SpringApplication springApplication = new  SpringApplication(LibraryApplication.class);
						  springApplication.addListeners( new ApplicationPidFileWriter("library.pid"));
						  springApplication.run( args );
		log.info( "Library start!");
	}
}
