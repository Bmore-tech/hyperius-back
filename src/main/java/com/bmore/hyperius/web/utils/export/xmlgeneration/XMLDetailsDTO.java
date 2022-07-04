package com.bmore.hyperius.web.utils.export.xmlgeneration;

public class XMLDetailsDTO {
	String cantidad;
	String descripcion;
	String codigoProducto;
	String sKU;
	String unidadMedida;
	String piezasEmpaque;
	String precioBruto;
	String montoBruto;
	String descuento;
	String montoDescuento;
	String precioNeto;
	String montoNeto;
	String iva;
	String montoIVA;
	String montoTotalItem;
	String alfaNum1;
	String alfaNum2;
	String alfaNum3;
	String alfaNum4;
	String alfaNum5;
	public String getCantidad() {
		return cantidad;
	}
	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getCodigoProducto() {
		return codigoProducto;
	}
	public void setCodigoProducto(String codigoProducto) {
		this.codigoProducto = codigoProducto;
	}
	public String getsKU() {
		return sKU;
	}
	public void setsKU(String sKU) {
		this.sKU = sKU;
	}
	public String getUnidadMedida() {
		return unidadMedida;
	}
	public void setUnidadMedida(String unidadMedida) {
		this.unidadMedida = unidadMedida;
	}
	public String getPiezasEmpaque() {
		return piezasEmpaque;
	}
	public void setPiezasEmpaque(String piezasEmpaque) {
		this.piezasEmpaque = piezasEmpaque;
	}
	public String getPrecioBruto() {
		return precioBruto;
	}
	public void setPrecioBruto(String precioBruto) {
		this.precioBruto = precioBruto;
	}
	public String getMontoBruto() {
		return montoBruto;
	}
	public void setMontoBruto(String montoBruto) {
		this.montoBruto = montoBruto;
	}
	public String getDescuento() {
		return descuento;
	}
	public void setDescuento(String descuento) {
		this.descuento = descuento;
	}
	public String getMontoDescuento() {
		return montoDescuento;
	}
	public void setMontoDescuento(String montoDescuento) {
		this.montoDescuento = montoDescuento;
	}
	public String getPrecioNeto() {
		return precioNeto;
	}
	public void setPrecioNeto(String precioNeto) {
		this.precioNeto = precioNeto;
	}
	public String getMontoNeto() {
		return montoNeto;
	}
	public void setMontoNeto(String montoNeto) {
		this.montoNeto = montoNeto;
	}
	public String getIva() {
		return iva;
	}
	public void setIva(String iva) {
		this.iva = iva;
	}
	public String getMontoIVA() {
		return montoIVA;
	}
	public void setMontoIVA(String montoIVA) {
		this.montoIVA = montoIVA;
	}
	public String getMontoTotalItem() {
		return montoTotalItem;
	}
	public void setMontoTotalItem(String montoTotalItem) {
		this.montoTotalItem = montoTotalItem;
	}
	public String getAlfaNum1() {
		return alfaNum1;
	}
	public void setAlfaNum1(String alfaNum1) {
		this.alfaNum1 = alfaNum1;
	}
	public String getAlfaNum2() {
		return alfaNum2;
	}
	public void setAlfaNum2(String alfaNum2) {
		this.alfaNum2 = alfaNum2;
	}
	public String getAlfaNum3() {
		return alfaNum3;
	}
	public void setAlfaNum3(String alfaNum3) {
		this.alfaNum3 = alfaNum3;
	}
	public String getAlfaNum4() {
		return alfaNum4;
	}
	public void setAlfaNum4(String alfaNum4) {
		this.alfaNum4 = alfaNum4;
	}
	public String getAlfaNum5() {
		return alfaNum5;
	}
	public void setAlfaNum5(String alfaNum5) {
		this.alfaNum5 = alfaNum5;
	}
	@Override
	public String toString() {
		return "XMLDetailsDTO [cantidad=" + cantidad + ", descripcion="
				+ descripcion + ", codigoProducto=" + codigoProducto + ", sKU="
				+ sKU + ", unidadMedida=" + unidadMedida + ", piezasEmpaque="
				+ piezasEmpaque + ", precioBruto=" + precioBruto
				+ ", montoBruto=" + montoBruto + ", descuento=" + descuento
				+ ", montoDescuento=" + montoDescuento + ", precioNeto="
				+ precioNeto + ", montoNeto=" + montoNeto + ", iva=" + iva
				+ ", montoIVA=" + montoIVA + ", montoTotalItem="
				+ montoTotalItem + ", alfaNum1=" + alfaNum1 + ", alfaNum2="
				+ alfaNum2 + ", alfaNum3=" + alfaNum3 + ", alfaNum4="
				+ alfaNum4 + ", alfaNum5=" + alfaNum5 + "]";
	}
	
	public static XMLDetailsDTO XMLDetailsDTOEmpty(XMLDetailsDTO detailsDTO){
		detailsDTO = new XMLDetailsDTO("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
		return detailsDTO;
	}
	
	public XMLDetailsDTO(String cantidad, String descripcion,
			String codigoProducto, String sKU, String unidadMedida,
			String piezasEmpaque, String precioBruto, String montoBruto,
			String descuento, String montoDescuento, String precioNeto,
			String montoNeto, String iva, String montoIVA,
			String montoTotalItem, String alfaNum1, String alfaNum2,
			String alfaNum3, String alfaNum4, String alfaNum5) {
		super();
		this.cantidad = cantidad;
		this.descripcion = descripcion;
		this.codigoProducto = codigoProducto;
		this.sKU = sKU;
		this.unidadMedida = unidadMedida;
		this.piezasEmpaque = piezasEmpaque;
		this.precioBruto = precioBruto;
		this.montoBruto = montoBruto;
		this.descuento = descuento;
		this.montoDescuento = montoDescuento;
		this.precioNeto = precioNeto;
		this.montoNeto = montoNeto;
		this.iva = iva;
		this.montoIVA = montoIVA;
		this.montoTotalItem = montoTotalItem;
		this.alfaNum1 = alfaNum1;
		this.alfaNum2 = alfaNum2;
		this.alfaNum3 = alfaNum3;
		this.alfaNum4 = alfaNum4;
		this.alfaNum5 = alfaNum5;
	}
	
	
	public XMLDetailsDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
