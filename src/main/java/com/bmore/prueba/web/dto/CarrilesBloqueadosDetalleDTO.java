package com.bmore.prueba.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CarrilesBloqueadosDetalleDTO {

	private String vbeln;
	private String carrilBloqueado;
	private String user;
	private String proceso;
	private String marcaTiempo;

	public String getVbeln() {
		return vbeln;
	}

	public void setVbeln(String vbeln) {
		this.vbeln = vbeln;
	}

	public String getCarrilBloqueado() {
		return carrilBloqueado;
	}

	public void setCarrilBloqueado(String carrilBloqueado) {
		this.carrilBloqueado = carrilBloqueado;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getProceso() {
		return proceso;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public String getMarcaTiempo() {
		return marcaTiempo;
	}

	public void setMarcaTiempo(String marcaTiempo) {
		this.marcaTiempo = marcaTiempo;
	}

	@Override
	public String toString() {
		return "CarrilesBloqueadosDTO [carrilBloqueado=" + carrilBloqueado
				+ ", marcaTiempo=" + marcaTiempo + ", proceso=" + proceso
				+ ", user=" + user + ", vbeln=" + vbeln + "]";
	}

	public CarrilesBloqueadosDetalleDTO(String vbeln, String carrilBloqueado,
			String user, String proceso, String marcaTiempo) {
		super();
		this.vbeln = vbeln;
		this.carrilBloqueado = carrilBloqueado;
		this.user = user;
		this.proceso = proceso;
		this.marcaTiempo = marcaTiempo;
	}

	public CarrilesBloqueadosDetalleDTO() {
		super();
	}

}
