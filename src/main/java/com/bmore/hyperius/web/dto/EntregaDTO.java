package com.bmore.hyperius.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EntregaDTO {

	private String entrega;
	private String fechaDocumento;
	private String proveedor;
	private String proveedorDesc;
	private String werks;
	private String contabilizar;
	private String contabilizada;
	private String picking;
	private String me;
	private String lfart;
	private String werksBCPS;
	private String embalarEntrega;
	private String lgort;

	private EntregaDetalleDTOItem items;
	private CarrilesUbicacionDTO carriles;

	private ResultDTO resultDT;

	public String getEntrega() {
		return entrega;
	}

	public void setEntrega(String entrega) {
		this.entrega = entrega;
	}

	public String getFechaDocumento() {
		return fechaDocumento;
	}

	public void setFechaDocumento(String fechaDocumento) {
		this.fechaDocumento = fechaDocumento;
	}

	public String getProveedor() {
		return proveedor;
	}

	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}

	public String getProveedorDesc() {
		return proveedorDesc;
	}

	public void setProveedorDesc(String proveedorDesc) {
		this.proveedorDesc = proveedorDesc;
	}

	public void setWerks(String werks) {
		this.werks = werks;
	}

	public String getWerks() {
		return werks;
	}

	public void setItems(EntregaDetalleDTOItem items) {
		this.items = items;
	}

	public EntregaDetalleDTOItem getItems() {
		return items;
	}

	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}

	public ResultDTO getResultDT() {
		return resultDT;
	}

	public void setCarriles(CarrilesUbicacionDTO carriles) {
		this.carriles = carriles;
	}

	public CarrilesUbicacionDTO getCarriles() {
		return carriles;
	}

	public void setContabilizar(String contabilizar) {
		this.contabilizar = contabilizar;
	}

	public String getContabilizar() {
		return contabilizar;
	}

	public void setMe(String me) {
		this.me = me;
	}

	public String getMe() {
		return me;
	}

	public void setPicking(String picking) {
		this.picking = picking;
	}

	public String getPicking() {
		return picking;
	}

	public void setContabilizada(String contabilizada) {
		this.contabilizada = contabilizada;
	}

	public String getContabilizada() {
		return contabilizada;
	}

	public void setLfart(String lfart) {
		this.lfart = lfart;
	}

	public String getLfart() {
		return lfart;
	}

	public void setWerksBCPS(String werksBCPS) {
		this.werksBCPS = werksBCPS;
	}

	public String getWerksBCPS() {
		return werksBCPS;
	}

	@Override
	public String toString() {
		return "EntregaDTO [carriles=" + carriles + ", contabilizada="
				+ contabilizada + ", contabilizar=" + contabilizar
				+ ", embalarEntrega=" + embalarEntrega + ", entrega=" + entrega
				+ ", fechaDocumento=" + fechaDocumento + ", items=" + items
				+ ", lfart=" + lfart + ", lgort=" + lgort + ", me=" + me
				+ ", picking=" + picking + ", proveedor=" + proveedor
				+ ", proveedorDesc=" + proveedorDesc + ", resultDT=" + resultDT
				+ ", werks=" + werks + ", werksBCPS=" + werksBCPS + "]";
	}

	public void setEmbalarEntrega(String embalarEntrega) {
		this.embalarEntrega = embalarEntrega;
	}

	public String getEmbalarEntrega() {
		return embalarEntrega;
	}

	public void setLgort(String lgort) {
		this.lgort = lgort;
	}

	public String getLgort() {
		return lgort;
	}

}
