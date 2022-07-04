package com.bmore.prueba.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PlaneacionBodyDTO {

	String centro;
	String cajas;
	String embarques;

	public String getCentro() {
		return centro;
	}

	public void setCentro(String centro) {
		this.centro = centro;
	}

	public String getCajas() {
		return cajas;
	}

	public void setCajas(String cajas) {
		this.cajas = cajas;
	}

	public String getEmbarques() {
		return embarques;
	}

	public void setEmbarques(String embarques) {
		this.embarques = embarques;
	}

	@Override
	public String toString() {
		return "PlaneacionBodyDTO [cajas=" + cajas + ", centro=" + centro
				+ ", embarques=" + embarques + "]";
	}

	public PlaneacionBodyDTO(String centro, String cajas, String embarques) {
		super();
		this.centro = centro;
		this.cajas = cajas;
		this.embarques = embarques;
	}

	public PlaneacionBodyDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

}
