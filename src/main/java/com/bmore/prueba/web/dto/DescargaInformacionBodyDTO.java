package com.bmore.prueba.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DescargaInformacionBodyDTO {
	String werks;
	String folioTransporte;
	String entregasEntrantes;
	String entregasSalientes;
	String ordenesProduccion;
	String unidadesManipulacion;

	public String getWerks() {
		return werks;
	}

	public void setWerks(String werks) {
		this.werks = werks;
	}

	public String getFolioTransporte() {
		return folioTransporte;
	}

	public void setFolioTransporte(String folioTransporte) {
		this.folioTransporte = folioTransporte;
	}

	public String getEntregasEntrantes() {
		return entregasEntrantes;
	}

	public void setEntregasEntrantes(String entregasEntrantes) {
		this.entregasEntrantes = entregasEntrantes;
	}

	public String getEntregasSalientes() {
		return entregasSalientes;
	}

	public void setEntregasSalientes(String entregasSalientes) {
		this.entregasSalientes = entregasSalientes;
	}

	public String getOrdenesProduccion() {
		return ordenesProduccion;
	}

	public void setOrdenesProduccion(String ordenesProduccion) {
		this.ordenesProduccion = ordenesProduccion;
	}

	public String getUnidadesManipulacion() {
		return unidadesManipulacion;
	}

	public void setUnidadesManipulacion(String unidadesManipulacion) {
		this.unidadesManipulacion = unidadesManipulacion;
	}

	@Override
	public String toString() {
		return "DescargaInformacionBodyDTO [entregasEntrantes="
				+ entregasEntrantes + ", entregasSalientes="
				+ entregasSalientes + ", folioTransporte=" + folioTransporte
				+ ", ordenesProduccion=" + ordenesProduccion
				+ ", unidadesManipulacion=" + unidadesManipulacion + ", werks="
				+ werks + "]";
	}

	public DescargaInformacionBodyDTO(String werks, String folioTransporte,
			String entregasEntrantes, String entregasSalientes,
			String ordenesProduccion, String unidadesManipulacion) {
		super();
		this.werks = werks;
		this.folioTransporte = folioTransporte;
		this.entregasEntrantes = entregasEntrantes;
		this.entregasSalientes = entregasSalientes;
		this.ordenesProduccion = ordenesProduccion;
		this.unidadesManipulacion = unidadesManipulacion;
	}

	public DescargaInformacionBodyDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

}
