package com.itau.srv.trading.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.itau.srv.trading.service", "com.itau.common.library"})
public class ServicoDeTradingApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.directory("env")
				.ignoreIfMalformed()
				.ignoreIfMissing()
				.load();

		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);
		SpringApplication.run(ServicoDeTradingApplication.class, args);
	}
}
