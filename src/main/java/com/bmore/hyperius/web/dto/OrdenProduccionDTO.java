package com.bmore.hyperius.web.dto;

public class OrdenProduccionDTO {

	private String ordenProduccion;
	private String fechaDocumento;
	private String fabrica;
	private String fabricaDesc;
	private String werks;
	private String contabilizar;

	private String contabilizada;
	private String picking;
	private String tipoAlmacen;
	private String admin;

	private OrdenProduccionDetalleDTOItem items;
	private CarrilesUbicacionDTO carriles;
	private ResultDTO resultDT;

	public String getOrdenProduccion() {
		return ordenProduccion;
	}

	public void setOrdenProduccion(String ordenProduccion) {
		this.ordenProduccion = ordenProduccion;
	}

	public String getFechaDocumento() {
		return fechaDocumento;
	}

	public void setFechaDocumento(String fechaDocumento) {
		this.fechaDocumento = fechaDocumento;
	}

	public String getFabrica() {
		return fabrica;
	}

	public void setFabrica(String fabrica) {
		this.fabrica = fabrica;
	}

	public String getFabricaDesc() {
		return fabricaDesc;
	}

	public void setFabricaDesc(String fabricaDesc) {
		this.fabricaDesc = fabricaDesc;
	}

	public OrdenProduccionDetalleDTOItem getItems() {
		return items;
	}

	public void setItems(OrdenProduccionDetalleDTOItem items) {
		this.items = items;
	}

	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}

	public ResultDTO getResultDT() {
		return resultDT;
	}

	public void setWerks(String werks) {
		this.werks = werks;
	}

	public String getWerks() {
		return werks;
	}

	public void setContabilizar(String contabilizar) {
		this.contabilizar = contabilizar;
	}

	public String getContabilizar() {
		return contabilizar;
	}

	public void setCarriles(CarrilesUbicacionDTO carriles) {
		this.carriles = carriles;
	}

	public CarrilesUbicacionDTO getCarriles() {
		return carriles;
	}

	public void setContabilizada(String contabilizada) {
		this.contabilizada = contabilizada;
	}

	public String getContabilizada() {
		return contabilizada;
	}

	public void setPicking(String picking) {
		this.picking = picking;
	}

	public String getPicking() {
		return picking;
	}

	public void setTipoAlmacen(String tipoAlmacen) {
		this.tipoAlmacen = tipoAlmacen;
	}

	public String getTipoAlmacen() {
		return tipoAlmacen;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}

	public String getAdmin() {
		return admin;
	}

	@Override
	public String toString() {
		return "OrdenProduccionDTO [ordenProduccion=" + ordenProduccion + ", fechaDocumento=" + fechaDocumento
				+ ", fabrica=" + fabrica + ", fabricaDesc=" + fabricaDesc + ", werks=" + werks + ", contabilizar="
				+ contabilizar + ", contabilizada=" + contabilizada + ", picking=" + picking + ", tipoAlmacen="
				+ tipoAlmacen + ", admin=" + admin + ", items=" + items + ", carriles=" + carriles + ", resultDT="
				+ resultDT + "]";
	}

	
}
