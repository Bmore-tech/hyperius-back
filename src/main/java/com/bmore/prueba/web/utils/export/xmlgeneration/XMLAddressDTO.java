package com.bmore.prueba.web.utils.export.xmlgeneration;

public class XMLAddressDTO {

	private String gln;
	private String calle;
	private String noExterior;
	private String noInterior;
	private String colonia;
	private String localidad;
	private String municipio;
	private String estado;
	private String pais;
	private String codigoPostal;

	public String getCalle() {
		return calle;
	}
	public void setCalle(String calle) {
		this.calle = calle;
	}
	public String getNoExterior() {
		return noExterior;
	}
	public void setNoExterior(String noExterior) {
		this.noExterior = noExterior;
	}
	public String getNoInterior() {
		return noInterior;
	}
	public void setNoInterior(String noInterior) {
		this.noInterior = noInterior;
	}
	public String getColonia() {
		return colonia;
	}
	public void setColonia(String colonia) {
		this.colonia = colonia;
	}
	public String getLocalidad() {
		return localidad;
	}
	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}
	public String getMunicipio() {
		return municipio;
	}
	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}
	public String getGln() {
		return gln;
	}
	public void setGln(String gln) {
		this.gln = gln;
	}
	public String getCodigoPostal() {
		return codigoPostal;
	}
	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}
	@Override
	public String toString() {
		return "XMLAddressDTO [gln=" + gln + ", calle=" + calle
				+ ", noExterior=" + noExterior + ", noInterior=" + noInterior
				+ ", colonia=" + colonia + ", localidad=" + localidad
				+ ", municipio=" + municipio + ", estado=" + estado + ", pais="
				+ pais + ", codigoPostal=" + codigoPostal + "]";
	}
	public XMLAddressDTO(String gln, String calle, String noExterior,
			String noInterior, String colonia, String localidad,
			String municipio, String estado, String pais, String codigoPostal) {
		super();
		this.gln = gln;
		this.calle = calle;
		this.noExterior = noExterior;
		this.noInterior = noInterior;
		this.colonia = colonia;
		this.localidad = localidad;
		this.municipio = municipio;
		this.estado = estado;
		this.pais = pais;
		this.codigoPostal = codigoPostal;
	}
	
	public static XMLAddressDTO XMLAddressDTOEmpty(XMLAddressDTO addressDTO){
		addressDTO  = new XMLAddressDTO("", "", "", "", "", "", "", "", "", "");
		return addressDTO;
	}
	
	public XMLAddressDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	 
	
}
