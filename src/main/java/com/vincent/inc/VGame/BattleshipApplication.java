package com.vincent.inc.VGame;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class BattleshipApplication {

	@Value("${spring.profiles.active}")
	private String env = "?";

	public static void main(String[] args) {
		SpringApplication.run(BattleshipApplication.class, args);
	}

	@GetMapping("/_status/healthz")
	public String healthCheck() {
		return String.format("Battleship %s is up and running", env);
	}

}
