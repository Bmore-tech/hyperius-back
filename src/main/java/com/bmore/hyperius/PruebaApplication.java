package com.bmore.hyperius;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;

@SpringBootApplication(exclude = { WebMvcAutoConfiguration.class })
public class PruebaApplication {

  public static void main(String[] args) {
    SpringApplication.run(PruebaApplication.class, args);
  }

}

// {"WFLYCTL0080: Failed services" => {"jboss.deployment.unit.\"sentinel-backend.war\".undertow-deployment" => "java.lang.RuntimeException: java.lang.IllegalStateException: Unable to find ServletContextHandler for provided ServletContext
//     Caused by: java.lang.RuntimeException: java.lang.IllegalStateException: Unable to find ServletContextHandler for provided ServletContext
//     Caused by: java.lang.IllegalStateException: Unable to find ServletContextHandler for provided ServletContext"}}