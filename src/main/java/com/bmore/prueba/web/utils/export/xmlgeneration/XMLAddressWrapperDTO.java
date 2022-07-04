package com.bmore.prueba.web.utils.export.xmlgeneration;

public class XMLAddressWrapperDTO {

	private String rfc;
	private String nombre;
	private String numeroCliente;
	private String contacto;
	private XMLAddressDTO domicilioFiscal;
	private XMLAddressDTO domicilioExpedicion;
	public String getRfc() {
		return rfc;
	}
	public void setRfc(String rfc) {
		this.rfc = rfc;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getNumeroCliente() {
		return numeroCliente;
	}
	public void setNumeroCliente(String numeroCliente) {
		this.numeroCliente = numeroCliente;
	}
	public String getContacto() {
		return contacto;
	}
	public void setContacto(String contacto) {
		this.contacto = contacto;
	}
	public XMLAddressDTO getDomicilioFiscal() {
		return domicilioFiscal;
	}
	public void setDomicilioFiscal(XMLAddressDTO domicilioFiscal) {
		this.domicilioFiscal = domicilioFiscal;
	}
	public XMLAddressDTO getDomicilioExpedicion() {
		return domicilioExpedicion;
	}
	public void setDomicilioExpedicion(XMLAddressDTO domicilioExpedicion) {
		this.domicilioExpedicion = domicilioExpedicion;
	}
	public XMLAddressWrapperDTO(String rfc, String nombre,
			String numeroCliente, String contacto,
			XMLAddressDTO domicilioFiscal, XMLAddressDTO domicilioExpedicion) {
		super();
		this.rfc = rfc;
		this.nombre = nombre;
		this.numeroCliente = numeroCliente;
		this.contacto = contacto;
		this.domicilioFiscal = domicilioFiscal;
		this.domicilioExpedicion = domicilioExpedicion;
	}
	
	public static XMLAddressWrapperDTO XMLAddressWrapperDTOEmpty(XMLAddressWrapperDTO wrapperDTO){
		XMLAddressDTO domFisEmt = null;
		domFisEmt = XMLAddressDTO.XMLAddressDTOEmpty(domFisEmt);
		wrapperDTO =  new XMLAddressWrapperDTO("", "", "", "", domFisEmt,domFisEmt);
		return wrapperDTO;
	}
	
	
	
	public XMLAddressWrapperDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String toString() {
		return "XMLAddressWrapperDTO [rfc=" + rfc + ", nombre=" + nombre
				+ ", numeroCliente=" + numeroCliente + ", contacto=" + contacto
				+ ", domicilioFiscal=" + domicilioFiscal
				+ ", domicilioExpedicion=" + domicilioExpedicion + "]";
	}
	
	
}
