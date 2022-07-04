package com.bmore.hyperius.web.utils.export.report;

import java.sql.Date;
import java.sql.Time;

public class EDIGenerationDTO_Sec_A {

	Date fechZCont;
	Time horaZCont;
	String tknum;
	String vbeln;
	String sello;
	String caja;
	String sello_imp;
	String fer_origen;
	String fer_destino;
	String cve_origen;
	String cve_desitno;
	String edo_origen;
	String edo_destino;
	String sort1;
	String ntgew;

	public Date getFechZCont() {
		return fechZCont;
	}

	public void setFechZCont(Date fechZCont) {
		this.fechZCont = fechZCont;
	}

	public Time getHoraZCont() {
		return horaZCont;
	}

	public void setHoraZCont(Time horaZCont) {
		this.horaZCont = horaZCont;
	}

	public String getTknum() {
		return tknum;
	}

	public void setTknum(String tknum) {
		this.tknum = tknum;
	}

	public String getVbeln() {
		return vbeln;
	}

	public void setVbeln(String vbeln) {
		this.vbeln = vbeln;
	}

	public String getSello() {
		return sello;
	}

	public void setSello(String sello) {
		this.sello = sello;
	}

	public String getCaja() {
		return caja;
	}

	public void setCaja(String caja) {
		this.caja = caja;
	}

	public String getSello_imp() {
		return sello_imp;
	}

	public void setSello_imp(String sello_imp) {
		this.sello_imp = sello_imp;
	}

	public String getFer_origen() {
		return fer_origen;
	}

	public void setFer_origen(String fer_origen) {
		this.fer_origen = fer_origen;
	}

	public String getFer_destino() {
		return fer_destino;
	}

	public void setFer_destino(String fer_destino) {
		this.fer_destino = fer_destino;
	}

	public String getCve_origen() {
		return cve_origen;
	}

	public void setCve_origen(String cve_origen) {
		this.cve_origen = cve_origen;
	}

	public String getCve_desitno() {
		return cve_desitno;
	}

	public void setCve_desitno(String cve_desitno) {
		this.cve_desitno = cve_desitno;
	}

	public String getEdo_origen() {
		return edo_origen;
	}

	public void setEdo_origen(String edo_origen) {
		this.edo_origen = edo_origen;
	}

	public String getEdo_destino() {
		return edo_destino;
	}

	public void setEdo_destino(String edo_destino) {
		this.edo_destino = edo_destino;
	}

	public String getSort1() {
		return sort1;
	}

	public void setSort1(String sort1) {
		this.sort1 = sort1;
	}

	public String getNtgew() {
		return ntgew;
	}

	public void setNtgew(String ntgew) {
		this.ntgew = ntgew;
	}

	@Override
	public String toString() {
		return "EDIGenerationDTO [fechZCont=" + fechZCont + ", horaZCont="
				+ horaZCont + ", tknum=" + tknum + ", vbeln=" + vbeln
				+ ", sello=" + sello + ", caja=" + caja + ", sello_imp="
				+ sello_imp + ", fer_origen=" + fer_origen + ", fer_destino="
				+ fer_destino + ", cve_origen=" + cve_origen + ", cve_desitno="
				+ cve_desitno + ", edo_origen=" + edo_origen + ", edo_destino="
				+ edo_destino + ", sort1=" + sort1 + ", ntgew=" + ntgew + "]";
	}
	
	

}
