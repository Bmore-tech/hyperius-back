package com.bmore.hyperius.web.dto.SAP;

import java.io.Serializable;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoTable;

import lombok.Data;

@Data
public class E_Error_SapEntityDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1998988335341401287L;
	private String type;
	private String languaje;
	private String message;

	public E_Error_SapEntityDTO() {
		super();
		this.type = "S";
		// TODO Auto-generated constructor stub
	}

	public E_Error_SapEntityDTO(JCoTable errorTable) throws JCoException{
		super();
		this.type = errorTable.getString("TYPE");
		this.languaje = errorTable.getString("LANGUAJE");
		this.message = errorTable.getString("MESSAGE");
	}

	public E_Error_SapEntityDTO(String type, String languaje, String message) {
		super();
		this.type = type;
		this.languaje = languaje;
		this.message = message;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((languaje == null) ? 0 : languaje.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		E_Error_SapEntityDTO other = (E_Error_SapEntityDTO) obj;
		if (languaje == null) {
			if (other.languaje != null)
				return false;
		} else if (!languaje.equals(other.languaje))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "E_Error_SapEntity [type=" + type + ", languaje=" + languaje + ", message=" + message + "]";
	}

}
