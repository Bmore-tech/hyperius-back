package com.bmore.hyperius.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EntregaDetalleDTO {
	
	private String posicion;
	private String material;
	private String descripcion;
	private String hus;
	private String cajas;
	
	private String husPendientes;
	private String husAsignadas;
	private String me;
	private String tarima;
	private String lgnum;
	private String bestq;
	private String embalar;
	
	private ResultDTO resultDT;
	
	public String getPosicion() {
		return posicion;
	}
	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getHus() {
		return hus;
	}
	public void setHus(String hus) {
		this.hus = hus;
	}
	public String getCajas() {
		return cajas;
	}
	public void setCajas(String cajas) {
		this.cajas = cajas;
	}
	public String getHusAsignadas() {
		return husAsignadas;
	}
	public void setHusAsignadas(String husAsignadas) {
		this.husAsignadas = husAsignadas;
	}
	public void setMe(String me) {
		this.me = me;
	}
	public String getMe() {
		return me;
	}
	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}
	public ResultDTO getResultDT() {
		return resultDT;
	}
	public void setTarima(String tarima) {
		this.tarima = tarima;
	}
	public String getTarima() {
		return tarima;
	}
	public void setHusPendientes(String husPendientes) {
		this.husPendientes = husPendientes;
	}
	public String getHusPendientes() {
		return husPendientes;
	}
	public void setLgnum(String lgnum) {
		this.lgnum = lgnum;
	}
	public String getLgnum() {
		return lgnum;
	}
	public void setBestq(String bestq) {
		this.bestq = bestq;
	}
	public String getBestq() {
		return bestq;
	}
	public void setEmbalar(String embalar) {
		this.embalar = embalar;
	}
	public String getEmbalar() {
		return embalar;
	}
	
	

}
