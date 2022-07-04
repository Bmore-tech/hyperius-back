package com.bmore.hyperius.mobile.dto;

import java.util.HashMap;

import com.bmore.hyperius.mobile.utils.ResultDT;

/**
 * 
 * @author nesquivel
 */
public class EntregaInput {
	private String entrega;
	private String hu1;
	private String hu2;
	private String matnr;
	private String uDestino0;
	private String uDestino1;
	private String uDestino2;
	private String uOrigen0;
	private String uOrigen1;
	private String uOrigen2;
	private String matnrT;
	private String cant;
	private String cantT;
	private String maktx;
	private String confHus;
	private String confUbicacionDestino;
	private String lgort;
	private String um;
	private String checkBestq;
	private String lfart;
	private int numeroHus;
	private int pantallaMontacargas = 0;
	private HashMap<String, String> materiales;
	private String werks;
	private String usuarioMontacarga;
	private ResultDT resultDT;

	/**
	 * @return the entrega
	 */
	public String getEntrega() {
		return entrega;
	}

	/**
	 * @param entrega the entrega to set
	 */
	public void setEntrega(String entrega) {
		this.entrega = entrega;
	}

	/**
	 * @return the hu1
	 */
	public String getHu1() {
		return hu1;
	}

	/**
	 * @param hu1 the hu1 to set
	 */
	public void setHu1(String hu1) {
		this.hu1 = hu1;
	}

	/**
	 * @return the hu2
	 */
	public String getHu2() {
		return hu2;
	}

	/**
	 * @param hu2 the hu2 to set
	 */
	public void setHu2(String hu2) {
		this.hu2 = hu2;
	}

	/**
	 * @return the matnr
	 */
	public String getMatnr() {
		return matnr;
	}

	/**
	 * @param matnr the matnr to set
	 */
	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}

	/**
	 * @return the uDestino1
	 */
	public String getuDestino1() {
		return uDestino1;
	}

	/**
	 * @param uDestino1 the uDestino1 to set
	 */
	public void setuDestino1(String uDestino1) {
		this.uDestino1 = uDestino1;
	}

	/**
	 * @return the uDestino2
	 */
	public String getuDestino2() {
		return uDestino2;
	}

	/**
	 * @param uDestino2 the uDestino2 to set
	 */
	public void setuDestino2(String uDestino2) {
		this.uDestino2 = uDestino2;
	}

	/**
	 * @return the matnrT
	 */
	public String getMatnrT() {
		return matnrT;
	}

	/**
	 * @param matnrT the matnrT to set
	 */
	public void setMatnrT(String matnrT) {
		this.matnrT = matnrT;
	}

	/**
	 * @return the cant
	 */
	public String getCant() {
		return cant;
	}

	/**
	 * @param cant the cant to set
	 */
	public void setCant(String cant) {
		this.cant = cant;
	}

	/**
	 * @return the cantT
	 */
	public String getCantT() {
		return cantT;
	}

	/**
	 * @param cantT the cantT to set
	 */
	public void setCantT(String cantT) {
		this.cantT = cantT;
	}

	/**
	 * @return the maktx
	 */
	public String getMaktx() {
		return maktx;
	}

	/**
	 * @param maktx the maktx to set
	 */
	public void setMaktx(String maktx) {
		this.maktx = maktx;
	}

	/**
	 * @return the confHus
	 */
	public String getConfHus() {
		return confHus;
	}

	/**
	 * @param confHus the confHus to set
	 */
	public void setConfHus(String confHus) {
		this.confHus = confHus;
	}

	public void setResultDT(ResultDT resultDT) {
		this.resultDT = resultDT;
	}

	public ResultDT getResultDT() {
		return resultDT;
	}

	public void setuOrigen1(String uOrigen1) {
		this.uOrigen1 = uOrigen1;
	}

	public String getuOrigen1() {
		return uOrigen1;
	}

	public void setuOrigen2(String uOrigen2) {
		this.uOrigen2 = uOrigen2;
	}

	public String getuOrigen2() {
		return uOrigen2;
	}

	public void setConfUbicacionDestino(String confUbicacionDestino) {
		this.confUbicacionDestino = confUbicacionDestino;
	}

	public String getConfUbicacionDestino() {
		return confUbicacionDestino;
	}

	public void setWerks(String werks) {
		this.werks = werks;
	}

	public String getWerks() {
		return werks;
	}

	public void setUsuarioMontacarga(String usuarioMontacarga) {
		this.usuarioMontacarga = usuarioMontacarga;
	}

	public String getUsuarioMontacarga() {
		return usuarioMontacarga;
	}

	public void setMateriales(HashMap<String, String> materiales) {
		this.materiales = materiales;
	}

	public HashMap<String, String> getMateriales() {
		return materiales;
	}

	public void setuDestino0(String uDestino0) {
		this.uDestino0 = uDestino0;
	}

	public String getuDestino0() {
		return uDestino0;
	}

	public void setPantallaMontacargas(int pantallaMontacargas) {
		this.pantallaMontacargas = pantallaMontacargas;
	}

	public int getPantallaMontacargas() {
		return pantallaMontacargas;
	}

	public void setLgort(String lgort) {
		this.lgort = lgort;
	}

	public String getLgort() {
		return lgort;
	}

	public void setuOrigen0(String uOrigen0) {
		this.uOrigen0 = uOrigen0;
	}

	public String getuOrigen0() {
		return uOrigen0;
	}

	public void setUm(String um) {
		this.um = um;
	}

	public String getUm() {
		return um;
	}

	public void setCheckBestq(String checkBestq) {
		this.checkBestq = checkBestq;
	}

	public String getCheckBestq() {
		return checkBestq;
	}

	public void setNumeroHus(int numeroHus) {
		this.numeroHus = numeroHus;
	}

	public int getNumeroHus() {
		return numeroHus;
	}

	public void setLfart(String lfart) {
		this.lfart = lfart;
	}

	public String getLfart() {
		return lfart;
	}

	@Override
	public String toString() {
		return "EntregaInput [cant=" + cant + ", cantT=" + cantT + ", checkBestq=" + checkBestq + ", confHus=" + confHus
				+ ", confUbicacionDestino=" + confUbicacionDestino + ", entrega=" + entrega + ", hu1=" + hu1 + ", hu2="
				+ hu2 + ", lfart=" + lfart + ", lgort=" + lgort + ", maktx=" + maktx + ", materiales=" + materiales
				+ ", matnr=" + matnr + ", matnrT=" + matnrT + ", numeroHus=" + numeroHus + ", pantallaMontacargas="
				+ pantallaMontacargas + ", resultDT=" + resultDT + ", uDestino0=" + uDestino0 + ", uDestino1="
				+ uDestino1 + ", uDestino2=" + uDestino2 + ", uOrigen0=" + uOrigen0 + ", uOrigen1=" + uOrigen1
				+ ", uOrigen2=" + uOrigen2 + ", um=" + um + ", usuarioMontacarga=" + usuarioMontacarga + ", werks="
				+ werks + "]";
	}

}
