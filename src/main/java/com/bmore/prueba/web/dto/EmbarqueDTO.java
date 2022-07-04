package com.bmore.prueba.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EmbarqueDTO {

	private String ordenEmbarque;
	private String fechaDocumento;
	private String fabrica;
	private String fabricaDesc;
	private String contabilizar;
	private String werks;
	private String contabilizada;
	private String picking;
	private String vkorg;
	private String lfart;
	private String transporte;
	private String tipoAlmacen;
	private String admin;

	private EmbarqueDetalleDTOItem items;

	private ResultDTO resultDT;
	private CarrilesUbicacionDTO carriles;

	public ResultDTO getResultDT() {
		return resultDT;
	}

	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}

	public EmbarqueDetalleDTOItem getItems() {
		return items;
	}

	public void setItems(EmbarqueDetalleDTOItem items) {
		this.items = items;
	}

	public String getOrdenEmbarque() {
		return ordenEmbarque;
	}

	public void setOrdenEmbarque(String ordenEmbarque) {
		this.ordenEmbarque = ordenEmbarque;
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

	public void setWerks(String werks) {
		this.werks = werks;
	}

	public String getWerks() {
		return werks;
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

	public void setVkorg(String vkorg) {
		this.vkorg = vkorg;
	}

	public String getVkorg() {
		return vkorg;
	}

	public void setLfart(String lfart) {
		this.lfart = lfart;
	}

	public String getLfart() {
		return lfart;
	}

	public void setTransporte(String transporte) {
		this.transporte = transporte;
	}

	public String getTransporte() {
		return transporte;
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

}
