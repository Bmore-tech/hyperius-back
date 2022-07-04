package com.bmore.prueba.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NormaEmbalajeDTO {

	private String cantidad;
	private String tarima;
	private String descripcionTarima;
	private String letyp;
	private String legnum;
	private String unidadMedida;
	private String matnr;
	private String maktx;

	private String cantidad2;
	private String unidadMedida2;

	private String umren;
	private String umrez;

	private String pobjid;

	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}

	public String getCantidad() {
		return cantidad;
	}

	public void setTarima(String tarima) {
		this.tarima = tarima;
	}

	public String getTarima() {
		return tarima;
	}

	public void setDescripcionTarima(String descripcionTarima) {
		this.descripcionTarima = descripcionTarima;
	}

	public String getDescripcionTarima() {
		return descripcionTarima;
	}

	public void setLetyp(String letyp) {
		this.letyp = letyp;
	}

	public String getLetyp() {
		return letyp;
	}

	public void setLegnum(String legnum) {
		this.legnum = legnum;
	}

	public String getLegnum() {
		return legnum;
	}

	public void setUnidadMedida(String unidadMedida) {
		this.unidadMedida = unidadMedida;
	}

	public String getUnidadMedida() {
		return unidadMedida;
	}

	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}

	public String getMatnr() {
		return matnr;
	}

	public void setMaktx(String maktx) {
		this.maktx = maktx;
	}

	public String getMaktx() {
		return maktx;
	}

	public void setUmren(String umren) {
		this.umren = umren;
	}

	public String getUmren() {
		return umren;
	}

	public void setUmrez(String umrez) {
		this.umrez = umrez;
	}

	public String getUmrez() {
		return umrez;
	}

	public void setCantidad2(String cantidad2) {
		this.cantidad2 = cantidad2;
	}

	public String getCantidad2() {
		return cantidad2;
	}

	public void setUnidadMedida2(String unidadMedida2) {
		this.unidadMedida2 = unidadMedida2;
	}

	public String getUnidadMedida2() {
		return unidadMedida2;
	}

	public String getPobjid() {
		return pobjid;
	}

	public void setPobjid(String pobjid) {
		this.pobjid = pobjid;
	}

}
