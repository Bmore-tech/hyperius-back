package com.bmore.hyperius.mobile.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.bmore.hyperius.mobile.utils.ResultDT;

@XmlRootElement
public class CarrilUbicacionDTO {

	private String bloquearCarril;
	private String idCarril;
	private String cantidadHus;
	private String QuanHu;
	private String cajas;
	private String asignarHus;
	private String material;
	private String entrega;
	private String LGNUM;
	private String LGTYP;
	private String LGPLA;
	private String me;
	private String husPendientes;
	private String tipoAlmacen;
	private String idProceso;
	private String statusS;
	private String maxle;
	

	private ResultDT resultDT;

	public String getBloquearCarril() {
		return bloquearCarril;
	}

	public void setBloquearCarril(String bloquearCarril) {
		this.bloquearCarril = bloquearCarril;
	}

	public String getIdCarril() {
		return idCarril;
	}

	public void setIdCarril(String idCarril) {
		this.idCarril = idCarril;
	}

	public String getCantidadHus() {
		return cantidadHus;
	}

	public void setCantidadHus(String cantidadHus) {
		this.cantidadHus = cantidadHus;
	}

	public String getCajas() {
		return cajas;
	}

	public void setCajas(String cajas) {
		this.cajas = cajas;
	}

	public String getAsignarHus() {
		return asignarHus;
	}

	public void setAsignarHus(String asignarHus) {
		this.asignarHus = asignarHus;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getMaterial() {
		return material;
	}

	/**
	 * @return the entrega
	 */
	public String getEntrega() {
		return entrega;
	}

	/**
	 * @param entrega
	 *            the entrega to set
	 */
	public void setEntrega(String entrega) {
		this.entrega = entrega;
	}

	/**
	 * @return the lGNUM
	 */
	public String getLGNUM() {
		return LGNUM;
	}

	/**
	 * @param lGNUM
	 *            the lGNUM to set
	 */
	public void setLGNUM(String lGNUM) {
		LGNUM = lGNUM;
	}

	/**
	 * @return the lGTYP
	 */
	public String getLGTYP() {
		return LGTYP;
	}

	/**
	 * @param lGTYP
	 *            the lGTYP to set
	 */
	public void setLGTYP(String lGTYP) {
		LGTYP = lGTYP;
	}

	/**
	 * @return the lGPLA
	 */
	public String getLGPLA() {
		return LGPLA;
	}

	/**
	 * @param lGPLA
	 *            the lGPLA to set
	 */
	public void setLGPLA(String lGPLA) {
		LGPLA = lGPLA;
	}

	/**
	 * @return the quanHu
	 */
	public String getQuanHu() {
		return QuanHu;
	}

	/**
	 * @param quanHu
	 *            the quanHu to set
	 */
	public void setQuanHu(String quanHu) {
		QuanHu = quanHu;
	}

	public void setMe(String me) {
		this.me = me;
	}

	public String getMe() {
		return me;
	}

	public void setResultDT(ResultDT resultDT) {
		this.resultDT = resultDT;
	}

	public ResultDT getResultDT() {
		return resultDT;
	}

	public void setHusPendientes(String husPendientes) {
		this.husPendientes = husPendientes;
	}

	public String getHusPendientes() {
		return husPendientes;
	}

	public void setTipoAlmacen(String tipoAlmacen) {
		this.tipoAlmacen = tipoAlmacen;
	}

	public String getTipoAlmacen() {
		return tipoAlmacen;
	}

	public void setIdProceso(String idProceso) {
		this.idProceso = idProceso;
	}

	public String getIdProceso() {
		return idProceso;
	}

	public void setStatusS(String statusS) {
		this.statusS = statusS;
	}

	public String getStatusS() {
		return statusS;
	}

	public void setMaxle(String maxle) {
		this.maxle = maxle;
	}

	public String getMaxle() {
		return maxle;
	}

}
