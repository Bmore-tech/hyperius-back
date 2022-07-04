package com.bmore.hyperius.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

/**
 * Configura el modelo <strong>MVC</strong> implementando la interface de Spring
 * {@link WebMvcConfigurer}, esta clase complementa la configuración de la clase
 * {@link WebAppInitializer} para el arranque de la aplicación.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 16-07-2020
 */
@Slf4j
@Configuration
@EnableWebMvc
@ComponentScan({ "com.bmore.prueba.config", "com.bmore.prueba.web.*", "com.bmore.prueba.mobile.*" })
public class WebMvcConfig implements WebMvcConfigurer {

  /**
   * Agregar los ResourcesHandlers para Swagger 2.
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
    registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    log.info("Configurando el CORS");
    registry.addMapping("/**")
        .allowedMethods("*")
        .allowedOrigins("*");
    // .allowCredentials(true).maxAge(3600);
  }
}
