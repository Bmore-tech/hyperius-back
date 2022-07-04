package com.bmore.prueba.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InventarioDetalleDTO {

	private String matnr;
	private String verme;
	private String charg;
	private String cantidadHus;
	private String lgort;
	private String lgnum;
	private String lgtyp;
	private String lgpla;
	private String lenum;

	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}

	public String getMatnr() {
		return matnr;
	}

	public void setVerme(String verme) {
		this.verme = verme;
	}

	public String getVerme() {
		return verme;
	}

	public void setCantidadHus(String cantidadHus) {
		this.cantidadHus = cantidadHus;
	}

	public String getCantidadHus() {
		return cantidadHus;
	}

	public void setLgnum(String lgnum) {
		this.lgnum = lgnum;
	}

	public String getLgnum() {
		return lgnum;
	}

	public void setLgtyp(String lgtyp) {
		this.lgtyp = lgtyp;
	}

	public String getLgtyp() {
		return lgtyp;
	}

	public void setLgpla(String lgpla) {
		this.lgpla = lgpla;
	}

	public String getLgpla() {
		return lgpla;
	}

	public void setCharg(String charg) {
		this.charg = charg;
	}

	public String getCharg() {
		return charg;
	}

	public void setLenum(String lenum) {
		this.lenum = lenum;
	}

	public String getLenum() {
		return lenum;
	}

	@Override
	public String toString() {
		return "InventarioDetalleDTO [cantidadHus=" + cantidadHus + ", charg="
				+ charg + ", lenum=" + lenum + ", lgnum=" + lgnum + ", lgpla="
				+ lgpla + ", lgtyp=" + lgtyp + ", matnr=" + matnr + ", verme="
				+ verme + "]";
	}

	public void setLgort(String lgort) {
		this.lgort = lgort;
	}

	public String getLgort() {
		return lgort;
	}

}