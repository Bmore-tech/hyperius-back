package com.bmore.hyperius.web.dto.SAP;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoTable;

import lombok.Data;

@Data
public class E_Mard_SapEntityDTO {

	private int docInvId;
	private String matnr;
	private String werks;
	private String lgort;
	private String labst;
	private String umlme;
	private String insme;
	private String einme;
	private String speme;
	private String retme;


	public E_Mard_SapEntityDTO(String matnr, String werks, String lgort, String labst, String umlme, String insme,
	        String einme, String speme, String retme) {
		super();
		this.matnr = matnr;
		this.werks = werks;
		this.lgort = lgort;
		this.labst = labst;
		this.umlme = umlme;
		this.insme = insme;
		this.einme = einme;
		this.speme = speme;
		this.retme = retme;
	}

	public E_Mard_SapEntityDTO() {
		super();
	}

	public E_Mard_SapEntityDTO(JCoTable jcoTable) throws JCoException {
		super();
		this.matnr = jcoTable.getString("MATNR");
		this.werks = jcoTable.getString("WERKS");
		this.lgort = jcoTable.getString("LGORT");
		this.labst = jcoTable.getString("LABST");
		this.umlme = jcoTable.getString("UMLME");
		this.insme = jcoTable.getString("INSME");
		this.einme = jcoTable.getString("EINME");
		this.speme = jcoTable.getString("SPEME");
		this.retme = jcoTable.getString("RETME");
	}
}
