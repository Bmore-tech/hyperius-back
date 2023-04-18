package com.bmore.hyperius.web.dto.SAP;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoTable;

import lombok.Data;

@Data
public class E_Msku_SapEntityDTO {

	private int docInvId;
	private String matnr;
	private String werks;
	private String kulab;
	private String kuins;
	private String kuein;

	public E_Msku_SapEntityDTO(String matnr, String werks, String kulab, String kuins, String kuein) {
		super();
		this.matnr = matnr;
		this.werks = werks;
		this.kulab = kulab;
		this.kuins = kuins;
		this.kuein = kuein;
	}

	public E_Msku_SapEntityDTO() {
		super();
	}

	public E_Msku_SapEntityDTO(JCoTable jcoTable) throws JCoException {
		super();
		this.matnr = jcoTable.getString("MATNR");
		this.werks = jcoTable.getString("WERKS");
		this.kulab = jcoTable.getString("KULAB");
		this.kuins = jcoTable.getString("KUINS");
		this.kuein = jcoTable.getString("KUEIN");
	}

	public E_Msku_SapEntityDTO(int docInvId, String matnr, String werks, String kulab, String kuins, String kuein) {
		super();
		this.docInvId = docInvId;
		this.matnr = matnr;
		this.werks = werks;
		this.kulab = kulab;
		this.kuins = kuins;
		this.kuein = kuein;
	}

	@Override
	public String toString() {
		return "E_Msku_SapEntity [docInvId=" + docInvId + ", matnr=" + matnr + ", werks=" + werks + ", kulab=" + kulab
		        + ", kuins=" + kuins + ", kuein=" + kuein + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + docInvId;
		result = prime * result + ((kuein == null) ? 0 : kuein.hashCode());
		result = prime * result + ((kuins == null) ? 0 : kuins.hashCode());
		result = prime * result + ((kulab == null) ? 0 : kulab.hashCode());
		result = prime * result + ((matnr == null) ? 0 : matnr.hashCode());
		result = prime * result + ((werks == null) ? 0 : werks.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		E_Msku_SapEntityDTO other = (E_Msku_SapEntityDTO) obj;
		if (docInvId != other.docInvId)
			return false;
		if (kuein == null) {
			if (other.kuein != null)
				return false;
		} else if (!kuein.equals(other.kuein))
			return false;
		if (kuins == null) {
			if (other.kuins != null)
				return false;
		} else if (!kuins.equals(other.kuins))
			return false;
		if (kulab == null) {
			if (other.kulab != null)
				return false;
		} else if (!kulab.equals(other.kulab))
			return false;
		if (matnr == null) {
			if (other.matnr != null)
				return false;
		} else if (!matnr.equals(other.matnr))
			return false;
		if (werks == null) {
			if (other.werks != null)
				return false;
		} else if (!werks.equals(other.werks))
			return false;
		return true;
	}
}
