package com.bmore.prueba.web.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bmore.prueba.web.dto.CarrilUbicacionDTO;
import com.bmore.prueba.web.dto.CarrilesUbicacionDTO;
import com.bmore.prueba.web.dto.EmbarqueDTO;
import com.bmore.prueba.web.dto.EmbarqueDetalleDTO;
import com.bmore.prueba.web.dto.EntregaInputDTO;
import com.bmore.prueba.web.dto.ResultDTO;
import com.bmore.prueba.web.repository.old.IMEmbarquePTRepositoryOld;
import com.bmore.prueba.web.service.IMEmbarquePTService;
import com.bmore.prueba.web.utils.Utils;

@Service
public class IMEmbarquePTServiceImpl implements IMEmbarquePTService {

	private static final Logger LOCATION = LoggerFactory.getLogger(IMEmbarquePTServiceImpl.class);

	@Override
	public EmbarqueDTO validaEmbarque(EmbarqueDTO embarque) {

		EmbarqueDTO embarqueReturn = new EmbarqueDTO();
		IMEmbarquePTRepositoryOld vidrioEmbarquePTDAO = new IMEmbarquePTRepositoryOld();
		ResultDTO resultDT = new ResultDTO();

		embarque.setTransporte(Utils.zeroFill(embarque.getOrdenEmbarque(), 10));

		// valida que entrega exista para centro

		resultDT = vidrioEmbarquePTDAO.obtieneEntregaDeTransporte(embarque.getTransporte());

		LOCATION.error("Result: " + resultDT.getId() + "entrega encontrada: " + resultDT.getTypeS());

		if (resultDT.getId() == 1) {

			embarque.setOrdenEmbarque(resultDT.getTypeS());
			embarqueReturn = vidrioEmbarquePTDAO.getEmbarque(embarque);

		} else {

			embarque.setOrdenEmbarque(embarque.getTransporte());
			LOCATION.error("Else " + embarque.getOrdenEmbarque());
			embarqueReturn = vidrioEmbarquePTDAO.getEmbarque(embarque);

			if (embarqueReturn.getResultDT().getId() == 1) {
				resultDT.setTypeS(embarque.getOrdenEmbarque());
			}

		}

		LOCATION.error("Result Embarque return: " + embarqueReturn.getResultDT().getId() + "entrega: "
				+ embarque.getOrdenEmbarque());

		switch (embarqueReturn.getResultDT().getId()) {

		case 1:// existe entrega

			resultDT.setId(embarqueReturn.getResultDT().getId());
			resultDT.setMsg(embarqueReturn.getResultDT().getMsg());

			embarqueReturn.setPicking("false");
			embarqueReturn.setContabilizar("false");
			embarqueReturn.setContabilizada("false");

			EmbarqueDTO embarqueReturn2 = new EmbarqueDTO();

			embarqueReturn2 = vidrioEmbarquePTDAO.getEmbarqueDetalle(embarque);

			int cont = 0;
			int x = 0;

			CarrilesUbicacionDTO carrilesList = new CarrilesUbicacionDTO();
			List<CarrilUbicacionDTO> carriles = new ArrayList<CarrilUbicacionDTO>();

			for (x = 0; x < embarqueReturn2.getItems().getItem().size(); x++) {

				CarrilesUbicacionDTO carrilesDAO = vidrioEmbarquePTDAO.getStock(embarque.getWerks(),
						embarqueReturn2.getItems().getItem().get(x).getMaterial());

				embarqueReturn2.getItems().getItem().get(x).setMaterial(Utils.zeroClean(

						embarqueReturn2.getItems().getItem().get(x).getMaterial()));

				for (int y = 0; y < carrilesDAO.getItem().size(); y++) {

					carrilesDAO.getItem().get(y).setMaterial(embarqueReturn2.getItems().getItem().get(x).getMaterial());

					carrilesDAO.getItem().get(y).setIdCarril(carrilesDAO.getItem().get(y).getMaterial() + "_"
							+ carrilesDAO.getItem().get(y).getCantidadHus() + "_"
							+ carrilesDAO.getItem().get(y).getCajas() + "_" + carrilesDAO.getItem().get(y).getMe()

					);

					carriles.add(carrilesDAO.getItem().get(y));

				}

				// ////////

				try {
					embarqueReturn2.getItems().getItem().get(x).setCajasAsignadas(
							"" + new BigDecimal(embarqueReturn2.getItems().getItem().get(x).getCajasAsignadas())
									.setScale(3));
				} catch (Exception e) {

				}

				EmbarqueDetalleDTO item = embarqueReturn2.getItems().getItem().get(x);

				item.setMaterial(Utils.zeroClean(item.getMaterial()));

				try {

					if (new BigDecimal(item.getCajas().trim()).setScale(3, RoundingMode.HALF_UP).equals(
							new BigDecimal(item.getCajasAsignadas().trim()).setScale(3, RoundingMode.HALF_UP))) {
						cont++;
					}
				} catch (Exception e) {

					LOCATION.error("Error al sumar cajas: " + " " + e.toString());

				}
			}

			// ////////
			// carriles
			carrilesList.setItem(carriles);

			embarqueReturn.setCarriles(carrilesList);
			embarqueReturn.setItems(embarqueReturn2.getItems());

			if (cont == x) {// totalmente pickeado no permitir hacer
				// modificaciones
				ResultDTO resultDT2 = new ResultDTO();

				resultDT2 = vidrioEmbarquePTDAO.contabilizadoOK(embarque.getOrdenEmbarque());
				embarqueReturn.setPicking("false");
				if (resultDT2.getId() == 1) {

					embarqueReturn.setContabilizar("false");
					embarqueReturn.setContabilizada("true");
					embarqueReturn.setPicking("false");

				} else {
					embarqueReturn.setContabilizar("true");
					embarqueReturn.setContabilizada("false");
				}

			} else {

				EntregaInputDTO entregaInput = new EntregaInputDTO();
				entregaInput.setEntrega(embarque.getOrdenEmbarque());
				entregaInput.setWerks(embarque.getWerks());

				LOCATION.error("Error: entro a validar el ZPICKING:");

				EntregaInputDTO resultDT1 = vidrioEmbarquePTDAO.validarEntregaPickinCompleto(entregaInput);

				LOCATION.error("Error: entro a validar el ZPICKING: " + resultDT1.getResultDT().getId());

				if (resultDT1.getResultDT().getId() == 1) {

					embarqueReturn.setPicking("true");
					embarqueReturn.setContabilizar("false");
					embarqueReturn.setContabilizada("false");

				} else {
					embarqueReturn.setPicking("false");
					embarqueReturn.setContabilizar("false");
					embarqueReturn.setContabilizada("false");
				}
			}

			break;
		case 2:
			// Entrega no existe
			resultDT.setId(2);
			resultDT.setMsg(embarqueReturn.getResultDT().getMsg());
			break;

		case 5:
			resultDT.setId(2);
			// Entrega con error en datos de cabecera
			break;

		default:
			resultDT.setId(2);
			resultDT.setMsg("Engrega con error, case default en validaEntrega");
			break;
		}

		embarqueReturn.setResultDT(resultDT);

		return embarqueReturn;
	}

	@Override
	public ResultDTO ingresaDetalleEnvaseBO(CarrilesUbicacionDTO carriles, String user, String werks) {

		IMEmbarquePTRepositoryOld vidrioEmbarquePTDAO = new IMEmbarquePTRepositoryOld();

		for (int x = 0; x < carriles.getItem().size(); x++) {

			carriles.getItem().get(x).setMaterial(Utils.zeroFill(carriles.getItem().get(x).getMaterial(), 18));

			carriles.getItem().get(x).setEntrega(Utils.zeroFill(carriles.getItem().get(x).getEntrega(), 10));

		}

		return vidrioEmbarquePTDAO.ingresaDetalleEnvase(carriles.getItem().get(0).getEntrega(), carriles, user, werks);
	}

	@Override
	public ResultDTO contabilizarEntregaEntrante(EmbarqueDTO embarqueDTO, String user) {

		IMEmbarquePTRepositoryOld vidrioEmbarquePTDAO = new IMEmbarquePTRepositoryOld();
		// TransportesDAO transporteDAO = new TransportesDAO();

		embarqueDTO.setOrdenEmbarque(Utils.zeroFill(embarqueDTO.getOrdenEmbarque(), 10));

		ResultDTO resultDT = vidrioEmbarquePTDAO.contabilizaEntrega(embarqueDTO, user);

		switch (resultDT.getId()) {

		case 1:
			resultDT.setMsg("Entrega contabilizada con exito");
			break;
		case 4:
			resultDT.setMsg(
					"La entrega de salida no tiene asociado un transporte, contacte al departamento de sistemas");
			break;
		case 5:
			resultDT.setMsg("Error al ingresar inicio de carga");
			break;
		case 6:
			resultDT.setMsg("Error al ingresar registro 9 de contabilizacion");
			break;
		case 7:
			resultDT.setMsg("Error al ingresar registro 12 de doble facturacion");
			break;
		case 8:
			resultDT.setMsg("Error al actulizar el status del transporte");
			break;
		case 9:
			resultDT.setMsg("Entrega contabilizada con exito");
			break;
		case 10:
			resultDT.setMsg("Error al ingresar inicio de carga");
			break;
		case 11:
			resultDT.setMsg("Entrega contabilizada con exito");
			break;

		// 1 CORRECTO
		// 4 LA ENGREGA DE SALIDA NO TIENE ASOSCIADO UN TRANSPORTE, CONTACTE AL
		// DEPARTAMENTO DE SISTEMAS
		// 5 ERROR AL INSERTAR EN ZCONTINGENCIA CON ID_PROC
		// 6 ERROR AL INSERTAR EN ZCONTINGENCIA PROCESO 9

		// 7 ERROR AL INSERTAR EN ZCONTINGENCIA PROCESO 12

		// 8 ERROR AL ACTUALIZAR STATUS 5 TRANSPORTE
		// 9 ERROR AL INSERTAR EN ZCONTINGENCIA PROCESO 1
		// 10 ERROR AL INSERTAR EN ZCONTINGENCIA PROCESO 10
		// 11 NO ES FACTURABLE

		}

		return resultDT;
	}
}
