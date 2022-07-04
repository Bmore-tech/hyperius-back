package com.bmore.prueba.mobile.rest.response;

/**
 * Clase de respuesta por defecto de los servicios Rest.
 * 
 * @author Eduardo Chombo
 * @version 1.0
 * @since 06-11-2020
 */
public class DefaultResponse {

	private Integer responseCode;
	private String message;

	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
