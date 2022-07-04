package com.bmore.hyperius.mobile.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.bmore.hyperius.mobile.utils.ResultDT;

@XmlRootElement
public class OrdenProduccionDetalleDTO {
	private String posicion;
	private String material;
	private String descripcion;
	private String hus;
	private String cajas;
	private String cajasAsignadas;
	private String cajasPendientesAsignar;
	private String husAsignadas;
	private String husPendientes;
	private String me;
	private String lote;
	private String tarima;
	private String LGNUM;
	private String LGTYP;
	private String LGPLA;
	private String bestq;

	private ResultDT resultDT;

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

	public void setCajasAsignadas(String cajasAsignadas) {
		this.cajasAsignadas = cajasAsignadas;
	}

	public String getCajasAsignadas() {
		return cajasAsignadas;
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

	public void setLGNUM(String lGNUM) {
		LGNUM = lGNUM;
	}

	public String getLGNUM() {
		return LGNUM;
	}

	public void setLGTYP(String lGTYP) {
		LGTYP = lGTYP;
	}

	public String getLGTYP() {
		return LGTYP;
	}

	public void setLGPLA(String lGPLA) {
		LGPLA = lGPLA;
	}

	public String getLGPLA() {
		return LGPLA;
	}

	public void setResultDT(ResultDT resultDT) {
		this.resultDT = resultDT;
	}

	public ResultDT getResultDT() {
		return resultDT;
	}

	public void setCajasPendientesAsignar(String cajasPendientesAsignar) {
		this.cajasPendientesAsignar = cajasPendientesAsignar;
	}

	public String getCajasPendientesAsignar() {
		return cajasPendientesAsignar;
	}

	public void setHusPendientes(String husPendientes) {
		this.husPendientes = husPendientes;
	}

	public String getHusPendientes() {
		return husPendientes;
	}

	public void setTarima(String tarima) {
		this.tarima = tarima;
	}

	public String getTarima() {
		return tarima;
	}

	public void setBestq(String bestq) {
		this.bestq = bestq;
	}

	public String getBestq() {
		return bestq;
	}

	@Override
	public String toString() {
		return "OrdenProduccionDetalleDTO [LGNUM=" + LGNUM + ", LGPLA=" + LGPLA
				+ ", LGTYP=" + LGTYP + ", bestq=" + bestq + ", cajas=" + cajas
				+ ", cajasAsignadas=" + cajasAsignadas
				+ ", cajasPendientesAsignar=" + cajasPendientesAsignar
				+ ", descripcion=" + descripcion + ", hus=" + hus
				+ ", husAsignadas=" + husAsignadas + ", husPendientes="
				+ husPendientes + ", lote=" + lote + ", material=" + material
				+ ", me=" + me + ", posicion=" + posicion + ", resultDT="
				+ resultDT + ", tarima=" + tarima + "]";
	}

	
}
