package com.bmore.prueba.web.rest.resquest;

public class RemisionRequest {

	private String numEntrega;
	private String selloTransporte;
	private String tarjetaDe;
	private String placas;
	private String conductor;

	public String getNumEntrega() {
		return numEntrega;
	}

	public void setNumEntrega(String numEntrega) {
		this.numEntrega = numEntrega;
	}

	public String getSelloTransporte() {
		return selloTransporte;
	}

	public void setSelloTransporte(String selloTransporte) {
		this.selloTransporte = selloTransporte;
	}

	public String getTarjetaDe() {
		return tarjetaDe;
	}

	public void setTarjetaDe(String tarjetaDe) {
		this.tarjetaDe = tarjetaDe;
	}

	public String getPlacas() {
		return placas;
	}

	public void setPlacas(String placas) {
		this.placas = placas;
	}

	public String getConductor() {
		return conductor;
	}

	public void setConductor(String conductor) {
		this.conductor = conductor;
	}
}
