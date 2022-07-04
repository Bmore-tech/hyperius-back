package com.bmore.prueba.web.dto;

public class ReporteShippingDTO {

	private String contenedor;
	private String sello;
	private String booking;
	private String destino;
	private String buque;
	private String peso;
	private String naviera;
	private String folio;
	private String medida;
	private String tipo;
	private String aa;
	private String sku;

	public String getContenedor() {
		return contenedor;
	}

	public void setContenedor(String contenedor) {
		this.contenedor = contenedor;
	}

	public String getSello() {
		return sello;
	}

	public void setSello(String sello) {
		this.sello = sello;
	}

	public String getBooking() {
		return booking;
	}

	public void setBooking(String booking) {
		this.booking = booking;
	}

	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public String getBuque() {
		return buque;
	}

	public void setBuque(String buque) {
		this.buque = buque;
	}

	public String getPeso() {
		return peso;
	}

	public void setPeso(String peso) {
		this.peso = peso;
	}

	public String getNaviera() {
		return naviera;
	}

	public void setNaviera(String naviera) {
		this.naviera = naviera;
	}

	public String getFolio() {
		return folio;
	}

	public void setFolio(String folio) {
		this.folio = folio;
	}

	public String getMedida() {
		return medida;
	}

	public void setMedida(String medida) {
		this.medida = medida;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getAa() {
		return aa;
	}

	public void setAa(String aa) {
		this.aa = aa;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public ReporteShippingDTO(String contenedor, String sello,
			String booking, String destino, String buque, String peso,
			String naviera, String folio, String medida, String tipo,
			String aa, String sku) {
		super();
		this.contenedor = contenedor;
		this.sello = sello;
		this.booking = booking;
		this.destino = destino;
		this.buque = buque;
		this.peso = peso;
		this.naviera = naviera;
		this.folio = folio;
		this.medida = medida;
		this.tipo = tipo;
		this.aa = aa;
		this.sku = sku;
	}

	public ReporteShippingDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	
}
