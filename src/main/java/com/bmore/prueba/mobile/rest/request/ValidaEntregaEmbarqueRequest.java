package com.bmore.prueba.mobile.rest.request;

public class ValidaEntregaEmbarqueRequest {

	private String idRed;
	private String sessionId;
	private String entrega;
	private String werks;

	public String getIdRed() {
		return idRed;
	}

	public void setIdRed(String idRed) {
		this.idRed = idRed;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getEntrega() {
		return entrega;
	}

	public void setEntrega(String entrega) {
		this.entrega = entrega;
	}

	public String getWerks() {
		return werks;
	}

	public void setWerks(String werks) {
		this.werks = werks;
	}
}
