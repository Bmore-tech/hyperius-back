package com.bmore.prueba.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AlmacenDTO {

	private String lgort;
	private String lgnum;
	private String lgtyp;
	private String lgpla;
	private String charg;
	private String werks;

	public String getLgort() {
		return lgort;
	}

	public void setLgort(String lgort) {
		this.lgort = lgort;
	}

	public String getLgnum() {
		return lgnum;
	}

	public void setLgnum(String lgnum) {
		this.lgnum = lgnum;
	}

	public String getLgtyp() {
		return lgtyp;
	}

	public void setLgtyp(String lgtyp) {
		this.lgtyp = lgtyp;
	}

	public String getLgpla() {
		return lgpla;
	}

	public void setLgpla(String lgpla) {
		this.lgpla = lgpla;
	}

	public void setWerks(String werks) {
		this.werks = werks;
	}

	public String getWerks() {
		return werks;
	}

	public void setCharg(String charg) {
		this.charg = charg;
	}

	public String getCharg() {
		return charg;
	}

	@Override
	public String toString() {
		return "AlmacenDTO [charg=" + charg + ", lgnum=" + lgnum + ", lgort="
				+ lgort + ", lgpla=" + lgpla + ", lgtyp=" + lgtyp + ", werks="
				+ werks + "]";
	}

}
