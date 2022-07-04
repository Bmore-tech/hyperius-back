package com.bmore.hyperius.mobile.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EmbarqueDetalleDTO {
	
	private String posicion;
	private String material;
	private String descripcion;
	private String hus;
	private String cajas;
	private String cajasAsignadas;	
	private String husAsignadas;
	private String me;
	private String lote;
	private String vbeln;
		
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
	public void setLote(String lote) {
		this.lote = lote;
	}
	public String getLote() {
		return lote;
	}
	public void setCajasAsignadas(String cajasAsignadas) {
		this.cajasAsignadas = cajasAsignadas;
	}
	public String getCajasAsignadas() {
		return cajasAsignadas;
	}
	public void setVbeln(String vbeln) {
		this.vbeln = vbeln;
	}
	public String getVbeln() {
		return vbeln;
	}

	
	

}
