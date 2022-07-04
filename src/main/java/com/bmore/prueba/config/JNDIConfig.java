package com.bmore.prueba.config;

/**
 * Clase para la configuración de la conexión a la Base de Datos a través de
 * JNDI con Spring.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 26-08-2020
 *
 */
// @Configuration
// @ComponentScan({ "com.bmore.prueba.*" })
public class JNDIConfig {

	/**
	 * Establece la conexión con JNDI en un {@link DataSource}.
	 * 
	 * @return Un {@link DataSource} con la conexión.
	 * @throws NamingException En caso de que no haya encontrado el JNDI Name.
	 */
	// @Bean("dataSource")
	// public DataSource getDataSource() throws NamingException {
	// 	JndiTemplate jndiTemplate = new JndiTemplate();

	// 	return (DataSource) jndiTemplate.lookup("java:/BCPS");
	// }

	/**
	 * Realiza las consultas a través de {@link JdbcTemplate}.
	 * 
	 * @param dataSource Con los datos de la conexión.
	 * @return Un {@link JdbcTemplate} para la ejecución de SQL.
	 */
	// @Bean
	// public JdbcTemplate jdbcTemplate(DataSource dataSource) {
	// 	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	// 	jdbcTemplate.setResultsMapCaseInsensitive(true);

	// 	return jdbcTemplate;
	// }
}
