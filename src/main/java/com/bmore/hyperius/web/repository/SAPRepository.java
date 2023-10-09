package com.bmore.hyperius.web.repository;

import java.sql.SQLException;
import java.util.List;

import com.bmore.hyperius.web.dto.SAP.ZIACMF_I360_INV_MOV_2DTO;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

public interface SAPRepository {

	public static final String ZIACMF_I360_INV_MOV_2 = "ZIACMF_I360_INV_MOV_2";

	public ZIACMF_I360_INV_MOV_2DTO getSystemSnapshot_test(String werks, List<String> lgortLst,
			JCoDestination destination) throws JCoException, SQLException, RuntimeException;
	
}