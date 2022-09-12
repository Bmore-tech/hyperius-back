package com.bmore.hyperius;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;

@SpringBootApplication(exclude = { WebMvcAutoConfiguration.class })
public class HyperiusApplication {

  public static void main(String[] args) {
    SpringApplication.run(HyperiusApplication.class, args);
  }
}
