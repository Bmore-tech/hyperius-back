package com.bmore.prueba.web.utils.export.xmlgeneration;

public class XMLTotalDTO {

	
	private String moneda;
	private String factorConversion;
	private String totalFactura;
	private String montoDescuento1;
	private String montoTotalDescuento;
	private String subTotal;
	private String tipoImpTras2;
	private String iva;
	private String montoIva;
	private String totalImpTras;
	private String totalPagar;
	private String totalLetra;
	
	public String getMoneda() {
		return moneda;
	}
	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	public String getFactorConversion() {
		return factorConversion;
	}
	public void setFactorConversion(String factorConversion) {
		this.factorConversion = factorConversion;
	}
	public String getTotalFactura() {
		return totalFactura;
	}
	public void setTotalFactura(String totalFactura) {
		this.totalFactura = totalFactura;
	}
	public String getMontoDescuento1() {
		return montoDescuento1;
	}
	public void setMontoDescuento1(String montoDescuento1) {
		this.montoDescuento1 = montoDescuento1;
	}
	public String getMontoTotalDescuento() {
		return montoTotalDescuento;
	}
	public void setMontoTotalDescuento(String montoTotalDescuento) {
		this.montoTotalDescuento = montoTotalDescuento;
	}
	public String getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(String subTotal) {
		this.subTotal = subTotal;
	}
	public String getTipoImpTras2() {
		return tipoImpTras2;
	}
	public void setTipoImpTras2(String tipoImpTras2) {
		this.tipoImpTras2 = tipoImpTras2;
	}
	public String getIva() {
		return iva;
	}
	public void setIva(String iva) {
		this.iva = iva;
	}
	public String getMontoIva() {
		return montoIva;
	}
	public void setMontoIva(String montoIva) {
		this.montoIva = montoIva;
	}
	public String getTotalImpTras() {
		return totalImpTras;
	}
	public void setTotalImpTras(String totalImpTras) {
		this.totalImpTras = totalImpTras;
	}
	public String getTotalPagar() {
		return totalPagar;
	}
	public void setTotalPagar(String totalPagar) {
		this.totalPagar = totalPagar;
	}
	public String getTotalLetra() {
		return totalLetra;
	}
	public void setTotalLetra(String totalLetra) {
		this.totalLetra = totalLetra;
	}
	
	public static XMLTotalDTO XMLTotalDTOEmpty(XMLTotalDTO totalDTO){
		totalDTO =  new XMLTotalDTO("", "", "", "", "", "", "", "", "", "", "", "");
		return totalDTO;
	}
	
	public XMLTotalDTO(String moneda, String factorConversion,
			String totalFactura, String montoDescuento1,
			String montoTotalDescuento, String subTotal, String tipoImpTras2,
			String iva, String montoIva, String totalImpTras,
			String totalPagar, String totalLetra) {
		super();
		this.moneda = moneda;
		this.factorConversion = factorConversion;
		this.totalFactura = totalFactura;
		this.montoDescuento1 = montoDescuento1;
		this.montoTotalDescuento = montoTotalDescuento;
		this.subTotal = subTotal;
		this.tipoImpTras2 = tipoImpTras2;
		this.iva = iva;
		this.montoIva = montoIva;
		this.totalImpTras = totalImpTras;
		this.totalPagar = totalPagar;
		this.totalLetra = totalLetra;
	}
	public XMLTotalDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "XMLTotalDTO [moneda=" + moneda + ", factorConversion="
				+ factorConversion + ", totalFactura=" + totalFactura
				+ ", montoDescuento1=" + montoDescuento1
				+ ", montoTotalDescuento=" + montoTotalDescuento
				+ ", subTotal=" + subTotal + ", tipoImpTras2=" + tipoImpTras2
				+ ", iva=" + iva + ", montoIva=" + montoIva + ", totalImpTras="
				+ totalImpTras + ", totalPagar=" + totalPagar + ", totalLetra="
				+ totalLetra + "]";
	}

	
}
