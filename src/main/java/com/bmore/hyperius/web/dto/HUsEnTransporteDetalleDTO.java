package com.bmore.hyperius.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HUsEnTransporteDetalleDTO {

	private String matnr;
	private String idCarril;
	private String lgnum;
	private String lgtyp;
	private String lgpla;
	private String usuarioSupervisor;
	private String usuarioMontacarguista;
	private String idProceso;
	private String vbeln;
	private String tipoAlmacen;
	private String hu;
	private String status;
	private String husAsignadas;
	private String husEnTransporte;

	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}

	public String getMatnr() {
		return matnr;
	}

	public void setLgnum(String lgnum) {
		this.lgnum = lgnum;
	}

	public String getLgnum() {
		return lgnum;
	}

	public void setLgtyp(String lgtyp) {
		this.lgtyp = lgtyp;
	}

	public String getLgtyp() {
		return lgtyp;
	}

	public void setLgpla(String lgpla) {
		this.lgpla = lgpla;
	}

	public String getLgpla() {
		return lgpla;
	}

	public void setUsuarioSupervisor(String usuarioSupervisor) {
		this.usuarioSupervisor = usuarioSupervisor;
	}

	public String getUsuarioSupervisor() {
		return usuarioSupervisor;
	}

	public void setUsuarioMontacarguista(String usuarioMontacarguista) {
		this.usuarioMontacarguista = usuarioMontacarguista;
	}

	public String getUsuarioMontacarguista() {
		return usuarioMontacarguista;
	}

	public void setIdProceso(String idProceso) {
		this.idProceso = idProceso;
	}

	public String getIdProceso() {
		return idProceso;
	}

	public void setVbeln(String vbeln) {
		this.vbeln = vbeln;
	}

	public String getVbeln() {
		return vbeln;
	}

	public void setTipoAlmacen(String tipoAlmacen) {
		this.tipoAlmacen = tipoAlmacen;
	}

	public String getTipoAlmacen() {
		return tipoAlmacen;
	}

	public void setHu(String hu) {
		this.hu = hu;
	}

	public String getHu() {
		return hu;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setIdCarril(String idCarril) {
		this.idCarril = idCarril;
	}

	public String getIdCarril() {
		return idCarril;
	}

	public void setHusAsignadas(String husAsignadas) {
		this.husAsignadas = husAsignadas;
	}

	public String getHusAsignadas() {
		return husAsignadas;
	}

	public void setHusEnTransporte(String husEnTransporte) {
		this.husEnTransporte = husEnTransporte;
	}

	public String getHusEnTransporte() {
		return husEnTransporte;
	}
}