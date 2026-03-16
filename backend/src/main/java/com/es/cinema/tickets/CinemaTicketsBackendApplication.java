package com.es.cinema.tickets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CinemaTicketsBackendApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Fortaleza"));
		SpringApplication.run(CinemaTicketsBackendApplication.class, args);
	}

}
