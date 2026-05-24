package com.luisandro.Contactos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ContactosApplication {

	private static final Logger log = LogManager.getLogger(ContactosApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ContactosApplication.class, args);
		log.info("App começou a rodar");
	}

}
