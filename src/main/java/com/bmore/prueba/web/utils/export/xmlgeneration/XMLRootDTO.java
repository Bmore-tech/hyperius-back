package com.bmore.prueba.web.utils.export.xmlgeneration;

public class XMLRootDTO {

	private String numeroAprobacion;
	private String fechaAprobacion;
	private String termPagoDias;
	private String ordenCompra;
	private String fechaOrdenCompra;
	private String fechaVencimiento;
	private String folioInterno;
	public String getNumeroAprobacion() {
		return numeroAprobacion;
	}
	public void setNumeroAprobacion(String numeroAprobacion) {
		this.numeroAprobacion = numeroAprobacion;
	}
	public String getFechaAprobacion() {
		return fechaAprobacion;
	}
	public void setFechaAprobacion(String fechaAprobacion) {
		this.fechaAprobacion = fechaAprobacion;
	}
	public String getTermPagoDias() {
		return termPagoDias;
	}
	public void setTermPagoDias(String termPagoDias) {
		this.termPagoDias = termPagoDias;
	}
	public String getOrdenCompra() {
		return ordenCompra;
	}
	public void setOrdenCompra(String ordenCompra) {
		this.ordenCompra = ordenCompra;
	}
	public String getFechaVencimiento() {
		return fechaVencimiento;
	}
	public void setFechaVencimiento(String fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}
	public String getFolioInterno() {
		return folioInterno;
	}
	public void setFolioInterno(String folioInterno) {
		this.folioInterno = folioInterno;
	}

	public String getFechaOrdenCompra() {
		return fechaOrdenCompra;
	}
	public void setFechaOrdenCompra(String fechaOrdenCompra) {
		this.fechaOrdenCompra = fechaOrdenCompra;
	}
	public XMLRootDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public XMLRootDTO(String numeroAprobacion, String fechaAprobacion,
			String termPagoDias, String ordenCompra, String fechaOrdenCompra,
			String fechaVencimiento, String folioInterno) {
		super();
		this.numeroAprobacion = numeroAprobacion;
		this.fechaAprobacion = fechaAprobacion;
		this.termPagoDias = termPagoDias;
		this.ordenCompra = ordenCompra;
		this.fechaOrdenCompra = fechaOrdenCompra;
		this.fechaVencimiento = fechaVencimiento;
		this.folioInterno = folioInterno;
	}
	
	public static XMLRootDTO XMLRootDTOEmpty(XMLRootDTO rootDTO) {
		rootDTO = new XMLRootDTO("", "", "", "", "", "", "");
		return rootDTO;
	}
	
	@Override
	public String toString() {
		return "XMLRootDTO [numeroAprobacion=" + numeroAprobacion
				+ ", fechaAprobacion=" + fechaAprobacion + ", termPagoDias="
				+ termPagoDias + ", ordenCompra=" + ordenCompra
				+ ", fechaOrdenCompra=" + fechaOrdenCompra
				+ ", fechaVencimiento=" + fechaVencimiento + ", folioInterno="
				+ folioInterno + "]";
	}
	
	
	
}
