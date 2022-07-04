package com.bmore.prueba.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Configuración <strong>MVC</strong> para el arranque de la aplicación web,
 * sustituye la configuración convencional a través de el archivo físico
 * <code>web.xml</code>
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 16-07-2020
 */
@Configuration
@PropertySource("classpath:application.properties")
public class WebAppInitializer implements WebApplicationInitializer {

	/**
	 * Configuración del DispatcherServlet para el módulo de MVC.
	 */
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(WebMvcConfig.class);
		context.setServletContext(servletContext);

		ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcherServlet",
				new DispatcherServlet(context));

		servlet.setLoadOnStartup(1);
		servlet.addMapping("/");
		servlet.setInitParameter("throwExceptionIfNoHandlerFound", "true");
	}
}
