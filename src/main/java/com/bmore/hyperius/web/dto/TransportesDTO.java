package com.bmore.hyperius.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TransportesDTO {

	private ResultDTO resultDT;
	private int existe;
	private String idTransporte;
	private String idStatusTransporte;
	private String transporte;
	private String fechaPlaneada;
	private int idOperation;
	private String werks;

	public void setExiste(int existe) {
		this.existe = existe;
	}

	public int getExiste() {
		return existe;
	}

	public void setTransporte(String transporte) {
		this.transporte = transporte;
	}

	public String getTransporte() {
		return transporte;
	}

	public void setFechaPlaneada(String fechaPlaneada) {
		this.fechaPlaneada = fechaPlaneada;
	}

	public String getFechaPlaneada() {
		return fechaPlaneada;
	}

	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}

	public ResultDTO getResultDT() {
		return resultDT;
	}

	public void setIdTransporte(String idTransporte) {
		this.idTransporte = idTransporte;
	}

	public String getIdTransporte() {
		return idTransporte;
	}

	public void setIdOperation(int idOperation) {
		this.idOperation = idOperation;
	}

	public int getIdOperation() {
		return idOperation;
	}

	public void setIdStatusTransporte(String idStatusTransporte) {
		this.idStatusTransporte = idStatusTransporte;
	}

	public String getIdStatusTransporte() {
		return idStatusTransporte;
	}

	public void setWerks(String werks) {
		this.werks = werks;
	}

	public String getWerks() {
		return werks;
	}

}
