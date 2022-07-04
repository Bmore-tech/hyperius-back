package com.bmore.hyperius.web.utils.export.xmlgeneration;

public class XMLCreateDTO {

	XMLRootDTO root;
	XMLAddressWrapperDTO emisor;
	XMLAddressWrapperDTO receptor;
	XMLDetailsDTO detalles;
	XMLTotalDTO totales;
	XMLCustomDTO custom;
	
	public XMLRootDTO getRoot() {
		return root;
	}
	public void setRoot(XMLRootDTO root) {
		this.root = root;
	}
	public XMLAddressWrapperDTO getEmisor() {
		return emisor;
	}
	public void setEmisor(XMLAddressWrapperDTO emisor) {
		this.emisor = emisor;
	}
	public XMLAddressWrapperDTO getReceptor() {
		return receptor;
	}
	public void setReceptor(XMLAddressWrapperDTO receptor) {
		this.receptor = receptor;
	}
	public XMLDetailsDTO getDetalles() {
		return detalles;
	}
	public void setDetalles(XMLDetailsDTO detalles) {
		this.detalles = detalles;
	}
	public XMLTotalDTO getTotales() {
		return totales;
	}
	public void setTotales(XMLTotalDTO totales) {
		this.totales = totales;
	}
	public XMLCustomDTO getCustom() {
		return custom;
	}
	public void setCustom(XMLCustomDTO custom) {
		this.custom = custom;
	}
	@Override
	public String toString() {
		return "XMLCreateDTO [root=" + root + ", emisor=" + emisor
				+ ", receptor=" + receptor + ", detalles=" + detalles
				+ ", totales=" + totales + ", custom=" + custom + "]";
	}

	public static XMLCreateDTO XMLCreateDTOEmpty(XMLCreateDTO createDTO) {
		XMLRootDTO root = null;
		root = XMLRootDTO.XMLRootDTOEmpty(root);
		XMLAddressWrapperDTO emisor = null;
		emisor = XMLAddressWrapperDTO.XMLAddressWrapperDTOEmpty(emisor);
		XMLAddressWrapperDTO receptor = null;
		receptor = XMLAddressWrapperDTO.XMLAddressWrapperDTOEmpty(receptor);
		XMLDetailsDTO detalles = null;
		detalles = XMLDetailsDTO.XMLDetailsDTOEmpty(detalles);
		XMLTotalDTO totales = null;
		totales =  XMLTotalDTO.XMLTotalDTOEmpty(totales);
		XMLCustomDTO custom = null;
		custom = XMLCustomDTO.XMLCustomDTOEmpty(custom);
		createDTO = new XMLCreateDTO(root,emisor,receptor,detalles,totales,custom);
		return createDTO;
	}

	
	public XMLCreateDTO(XMLRootDTO root, XMLAddressWrapperDTO emisor,
			XMLAddressWrapperDTO receptor, XMLDetailsDTO detalles,
			XMLTotalDTO totales, XMLCustomDTO custom) {
		super();
		this.root = root;
		this.emisor = emisor;
		this.receptor = receptor;
		this.detalles = detalles;
		this.totales = totales;
		this.custom = custom;
	}
	public XMLCreateDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
