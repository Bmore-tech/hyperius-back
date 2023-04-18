package com.bmore.hyperius.web.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bmore.hyperius.web.dto.SAP.ZIACMF_I360_INV_MOV_2DTO;
import com.bmore.hyperius.web.repository.impl.SAPRepositoryImpl;
import com.bmore.hyperius.web.service.SAPConectorService;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SAPConectorServiceImpl implements SAPConectorService{

	//private Logger log = Logger.getLogger(SAPConectorServiceImpl.class.getName());
	// private ConnectionManager connectionManager = new ConnectionManager();
	// private SapOperationDao operationDao = new SapOperationDao();
	// private SAPRepository conciliationDao = new SAPRepository();
	
	@Override
	public void inventorySnapShot(JCoDestination destination) {// DocInvBean docInvBean, Connection con,
		try {
			// Se agrega Multi Almacen
			List<String> lgortLst = new ArrayList<>();
			lgortLst.add("MK01");
			SAPRepositoryImpl sapRepo = new SAPRepositoryImpl();
			ZIACMF_I360_INV_MOV_2DTO getSnapshot = sapRepo.getSystemSnapshot_test("HA00", lgortLst,
					destination);
			if (getSnapshot.geteError_SapEntities().getType().equals("S")
					&& getSnapshot.geteLqua_SapEntities() != null && getSnapshot.geteMard_SapEntities() != null
					&& getSnapshot.geteMsku_SapEntities() != null) {
				// operationDao.setZIACMF_I360_INV_MOV2(docInvBean, getSnapshot, con);
			} else {
				throw new Exception(
						"Ocurrio un error durante la extraccion MULTI-ALMACEN de la información para el documento de inventario: ");// +
																																	// docInvBean.getDocInvId()
			}

		} catch (SQLException e) {
			log.error(
					"[SapConciliationWorkService - inventorySnapShot] - Error SQL docId ",// + docInvBean.getDocInvId()
					e);
		}
		// catch (InvCicException e) {
		// log.log(Level.SEVERE, "[SapConciliationWorkService - inventorySnapShot] -
		// InvCicException docId "
		// + docInvBean.getDocInvId(), e);
		// }
		catch (JCoException e) {
			log.error(
					"[SapConciliationWorkService - inventorySnapShot] - Error JCO  docId ", //+ docInvBean.getDocInvId()
					e);
		} catch (RuntimeException e) {
			log.error("[SapConciliationWorkService - inventorySnapShot] - Error Runtime docId ");// +
																												// docInvBean.getDocInvId(),
																												// e
		} catch (Exception e) {
			log.error(
					"[SapConciliationWorkService - inventorySnapShot] - Exception docId " ,//+ docInvBean.getDocInvId()
					e);
		} finally {
			try {
				//con.close();
			} catch (Exception e) {
				log.error(
						"[SapConciliationWorkService] - Finally Exception docId ", e);// + docInvBean.getDocInvId()
			}
		}

	}

}
