package com.bmore.hyperius.mobile.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmore.hyperius.mobile.dto.EntregaInput;
import com.bmore.hyperius.mobile.repository.impl.EmbarquePTIMDAO;
import com.bmore.hyperius.mobile.utils.ResultDT;
import com.bmore.hyperius.mobile.utils.Utils;

public class EmbarquePTIMBO {

	private static final Logger LOCATION = LoggerFactory.getLogger(EmbarquePTIMBO.class);

	public static EntregaInput validarEntrega(EntregaInput entregaInput) throws ClassNotFoundException{

		entregaInput.setEntrega(Utils.zeroFill(entregaInput.getEntrega(), 10));

		// Limpiar para evitar errores con boton next de navegador
		entregaInput.setHu1(null);
		entregaInput.setHu2(null);
		entregaInput.setConfHus(null);

		EntregaInput entregaInput2 = new EntregaInput();
		EmbarquePTIMDAO embarquePTDAO = new EmbarquePTIMDAO();


		entregaInput2 = embarquePTDAO.validarEntregaPickin(entregaInput);

		ResultDT resultDT = entregaInput2.getResultDT();

		if (resultDT.getId() == 1) {

			entregaInput.setMateriales(entregaInput2.getMateriales());

			resultDT = embarquePTDAO.reservaHus(entregaInput);
			LOCATION.error("ID:" + resultDT.getId());

			switch (resultDT.getId()) {

			// 2 - No existe HU
			// 3 - HU ya consumida
			// 4 - No viene lote en HU de VEPO
			// 5 - Error al ingresar registro a MCHB
			// 6 - Error al consumir HU de VEKP
			// 7 - Error al ingresar a zcontingencia el hu

			case 1:

				entregaInput.setMatnr(Utils.zeroClean(resultDT.getTypeS()));// Cantidad
				// Material
				entregaInput.setCant("" + resultDT.getTypeF());// Cantidad
				// embalaje
				entregaInput.setCantT("" + resultDT.getTypeBD());
				entregaInput.setUm(resultDT.getMsg());

				resultDT = embarquePTDAO.obtieneDescripcionMaterial(resultDT.getTypeS(), entregaInput.getEntrega());

				entregaInput.setMaktx(resultDT.getTypeS());

				resultDT.setMsg("Lugar reservado");
				break;
			case 5:
				resultDT.setMsg("No hay mas hus por pickear");
				break;
			case 10:
				resultDT.setMsg("Error desconocido");
				break;

			}

		}
		LOCATION.error("antes de regresar de BO: " + entregaInput.getMatnr());
		entregaInput.setResultDT(resultDT);
		LOCATION.error(entregaInput.getResultDT().getMsg());
		return entregaInput;

	}

	public static EntregaInput consumirHU(EntregaInput entregaInput) throws ClassNotFoundException{

		entregaInput.setEntrega(Utils.zeroFill(entregaInput.getEntrega(), 10));

		// Limpiar para evitar errores con boton next de navegador

		ResultDT resultDT = new ResultDT();

		EmbarquePTIMDAO embarquePTDAO = new EmbarquePTIMDAO();

		entregaInput.setEntrega(Utils.zeroFill(entregaInput.getEntrega(), 10));

		resultDT = embarquePTDAO.consumeHUs(entregaInput);

		switch (resultDT.getId()) {

		// --1, HU consumida
		// --2, HU no disponible para consumo
		// --3, HU consumida previamente
		// --4, HU No corresponde con material de entrega
		// --5, Cantidad de HU no corresponde con la asignada
		// --6, HU Bloqueada
		// --7, HU en QA
		// --8, Error al actualizar HU
		// --9, HU no existe

		case 1:
			resultDT.setMsg("Hu ingresada correctamente");
			break;
		case 2:
			resultDT.setMsg("HU no disponible para consumo");
			break;
		case 3:
			resultDT.setMsg("HU consumida previamente");
			break;
		case 4:
			resultDT.setMsg("HU No corresponde con material de entrega");
			break;
		case 5:
			resultDT
					.setMsg("Cantidad de HU no corresponde con la asignada por supervisor");
			break;
		case 6:
			resultDT.setMsg("HU Bloqueada");
			break;
		case 7:
			//resultDT.setMsg("HU en QA, Â¿Desea incluirla en el embarque?"); solicitan bloquear las hus en Q
			resultDT.setMsg("HU Bloqueada");
			resultDT.setId(6);
			break;
		case 8:
			resultDT.setMsg("Error al actualizar HU");
			break;
		case 9:
			resultDT.setMsg("HU no existe");
			break;

		}

		LOCATION.error("antes de regresar de BO: " + entregaInput.getMatnr());
		entregaInput.setResultDT(resultDT);
		return entregaInput;

	}

	public static ResultDT limpiaPendientesXUsuario(String vbeln, String user) throws ClassNotFoundException{

		EmbarquePTIMDAO embarquePTDAO = new EmbarquePTIMDAO();

		return embarquePTDAO.limpiaPendientesXUsuario(vbeln, user);
	}

}