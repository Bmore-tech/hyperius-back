package com.bmore.hyperius.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EntregasTransportesDetalleDTO {

	private String vblenEntrante;
	private String vblenSaliente;
	private String tknum;
	private String status;
	private String lfart;
	private String edi;

	public void setVblenEntrante(String vblenEntrante) {
		this.vblenEntrante = vblenEntrante;
	}

	public String getVblenEntrante() {
		return vblenEntrante;
	}

	public void setVblenSaliente(String vblenSaliente) {
		this.vblenSaliente = vblenSaliente;
	}

	public String getVblenSaliente() {
		return vblenSaliente;
	}

	public void setTknum(String tknum) {
		this.tknum = tknum;
	}

	public String getTknum() {
		return tknum;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setLfart(String lfart) {
		this.lfart = lfart;
	}

	public String getLfart() {
		return lfart;
	}

	public String getEdi() {
		return edi;
	}

	public void setEdi(String edi) {
		this.edi = edi;
	}


	
}
