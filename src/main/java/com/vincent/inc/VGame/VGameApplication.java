package com.vincent.inc.VGame;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@RestController
public class VGameApplication {

	@Value("${spring.profiles.active}")
	private String env = "?";

	public static void main(String[] args) {
		SpringApplication.run(VGameApplication.class, args);
	}

	@GetMapping("/")
	public ModelAndView redirectSwagger(ModelMap model) {
		return new ModelAndView("redirect:/swagger-ui/index.html", model);
	}

	@GetMapping("/_status/healthz")
	public String healthCheck() {
		return String.format("VGame %s is up and running", env);
	}

}
