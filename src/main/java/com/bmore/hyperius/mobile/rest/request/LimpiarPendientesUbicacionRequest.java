package com.bmore.hyperius.mobile.rest.request;

public class LimpiarPendientesUbicacionRequest {

	private String ordeProduccion;
	private String usuarioMontacarga;
	private String sessionId;

	public String getOrdeProduccion() {
		return ordeProduccion;
	}

	public void setOrdeProduccion(String ordeProduccion) {
		this.ordeProduccion = ordeProduccion;
	}

	public String getUsuarioMontacarga() {
		return usuarioMontacarga;
	}

	public void setUsuarioMontacarga(String usuarioMontacarga) {
		this.usuarioMontacarga = usuarioMontacarga;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
