package com.bmore.hyperius.web.repository.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Repository;

import com.bmore.hyperius.web.dto.SAP.E_Error_SapEntityDTO;
import com.bmore.hyperius.web.dto.SAP.E_Lqua_SapEntityDTO;
import com.bmore.hyperius.web.dto.SAP.E_Mard_SapEntityDTO;
import com.bmore.hyperius.web.dto.SAP.E_Msku_SapEntityDTO;
import com.bmore.hyperius.web.dto.SAP.ZIACMF_I360_INV_MOV_2DTO;
import com.bmore.hyperius.web.repository.SAPRepository;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class SAPRepositoryImpl implements  SAPRepository{

//	private static final String ZIACMF_I360_MOV = "ZIACMF_I360_MOV";
	
	// private Logger log = Logger.getLogger(SAPRepositoryImpl.class.getName());
	// private final SapOperationDao operationDao = new SapOperationDao();
	
	@Override
	public ZIACMF_I360_INV_MOV_2DTO getSystemSnapshot_test(String werks, List<String> lgortLst,
			JCoDestination destination) throws JCoException, SQLException, RuntimeException {
		ZIACMF_I360_INV_MOV_2DTO ziacmf_I360_INV_MOV_2 = new ZIACMF_I360_INV_MOV_2DTO();
		try {
			List<E_Mard_SapEntityDTO> eMard_SapEntities = new ArrayList<>();
			List<E_Msku_SapEntityDTO> eMsku_SapEntities = new ArrayList<>();
			List<E_Lqua_SapEntityDTO> eLqua_SapEntities = new ArrayList<>();
			JCoFunction jcoFunction = destination.getRepository().getFunction(ZIACMF_I360_INV_MOV_2);
			JCoFunction jcoFunction2 = destination.getRepository().getFunction(ZIACMF_I360_INV_MOV_2);
			jcoFunction.getImportParameterList().setValue("I_WERKS", werks);
			jcoFunction2.getImportParameterList().setValue("I_WERKS", werks);

			JCoTable lgortTable = jcoFunction.getImportParameterList().getTable("I_R_LGORT");

			for (String lgort : lgortLst) {
				lgortTable.appendRow();
				lgortTable.setValue("SIGN", "I");
				lgortTable.setValue("OPTION", "EQ");
				lgortTable.setValue("LOW", lgort);
			}
			JCoTable lgortTable2 = jcoFunction2.getImportParameterList().getTable("I_R_LGORT");
			for (String lgort : lgortLst) {
				lgortTable2.appendRow();
				lgortTable2.setValue("SIGN", "I");
				lgortTable2.setValue("OPTION", "EQ");
				lgortTable2.setValue("LOW", lgort);
			}

			jcoFunction.execute(destination);
			jcoFunction2.execute(destination);
			JCoTable E_MARD = jcoFunction2.getExportParameterList().getTable("E_MARD");
			JCoTable E_MSKU = jcoFunction.getExportParameterList().getTable("E_MSKU");
			JCoTable E_LQUA = jcoFunction.getExportParameterList().getTable("E_LQUA");
//			JCoTable E_ERROR = jcoFunction.getExportParameterList().getTable("E_ERROR");
			E_Error_SapEntityDTO eError = new E_Error_SapEntityDTO();
			try {
				// eError = new E_Error_SapEntity(E_ERROR);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				log.info("SapConciliationDao: [ZIACMF_I360_INV_MOV_2 -getSystemSnapshot_MULTI_ALMACEN]: " + e1.getMessage());
			}
			if (eError.getType().equals("S")) {
				// Cycle of the E_MARD Export Table
				do {
					try {
						eMard_SapEntities.add(new E_Mard_SapEntityDTO(E_MARD));
					} catch (JCoException | RuntimeException e) {
						// Not Readable Row or EOF
						log.info(
								"SapConciliationDao: [ZIACMF_I360_INV_MOV_2 - getSystemSnapshot_MULTI_ALMACEN]: " + e.getMessage());
					}
				} while (E_MARD.nextRow());

				// Cycle of the E_MSKU Export Table
				do {
					try {
						eMsku_SapEntities.add(new E_Msku_SapEntityDTO(E_MSKU));
					} catch (JCoException | RuntimeException e) {
						// Not Readable Row or EOF
						log.info(
								"SapConciliationDao: [ZIACMF_I360_INV_MOV_2 - getSystemSnapshot_MULTI_ALMACEN]: " + e.getMessage());
					}
				} while (E_MSKU.nextRow());

				// Cycle of the E_LQUA Export Table
				do {
					try {
						eLqua_SapEntities.add(new E_Lqua_SapEntityDTO(E_LQUA));
					} catch (JCoException | RuntimeException e) {
						// Not Readable Row or EOF
						log.info(
								"SapConciliationDao: [ZIACMF_I360_INV_MOV_2 - getSystemSnapshot_MULTI_ALMACEN]: " + e.getMessage());
					}
				} while (E_LQUA.nextRow());
			}
			ziacmf_I360_INV_MOV_2.seteMard_SapEntities(eMard_SapEntities);
			ziacmf_I360_INV_MOV_2.seteMsku_SapEntities(eMsku_SapEntities);
			ziacmf_I360_INV_MOV_2.seteLqua_SapEntities(eLqua_SapEntities);
			ziacmf_I360_INV_MOV_2.seteError_SapEntities(eError);
		} catch (JCoException e) {
			throw e;
		} catch (RuntimeException e) {
			throw e;
		}
		return ziacmf_I360_INV_MOV_2;
	}
	
}
