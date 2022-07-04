package com.bmore.hyperius.web.rest.resquest;

public class ExportacionRequest {

	private String numEntrega;
	private String selloTransporte;
	private String talon;
	private String transportista;
	private String noCaja;
	private String selloImportador;

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

	public String getTalon() {
		return talon;
	}

	public void setTalon(String talon) {
		this.talon = talon;
	}

	public String getTransportista() {
		return transportista;
	}

	public void setTransportista(String transportista) {
		this.transportista = transportista;
	}

	public String getNoCaja() {
		return noCaja;
	}

	public void setNoCaja(String noCaja) {
		this.noCaja = noCaja;
	}

	public String getSelloImportador() {
		return selloImportador;
	}

	public void setSelloImportador(String selloImportador) {
		this.selloImportador = selloImportador;
	}
}
