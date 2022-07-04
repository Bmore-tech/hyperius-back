package com.bmore.prueba.web.repository;

import java.util.HashMap;
import java.util.List;

import com.bmore.prueba.web.dto.CarrilesUbicacionDTO;
import com.bmore.prueba.web.dto.OrdenProduccionDTO;
import com.bmore.prueba.web.dto.OrdenProduccionDetalleDTO;
import com.bmore.prueba.web.dto.OrdenProduccionInputDTO;
import com.bmore.prueba.web.dto.ResultDTO;

public interface AlimentacionLineaRepository {

	public Integer confirmaHUenDepa(OrdenProduccionInputDTO ordenProduccionInput, String hu);

	public Integer consumeInventario(String hu, OrdenProduccionInputDTO ordenProduccionInput);

	public OrdenProduccionDTO detalleOrdenProduccion(String aufnr, String werks);

	public CarrilesUbicacionDTO getCarriles(String WERKS, String MATNR, String ID_PR, String IP_PR_Z,
			HashMap<String, String> carrilesBloqueados, HashMap<String, String> carrilesMaterialBloqueado);

	public HashMap<String, String> getCarrilesBloqueados(String idProceso, String werks);

	public HashMap<String, String> getCarrilesMaterialBloqueado(String matnr, String werks);

	public OrdenProduccionDetalleDTO getDataHU(String hu, String werks, String lgtyp, String lgpla);

	public OrdenProduccionDTO getOrden(OrdenProduccionDTO ordenInput);

	public ResultDTO ingresaDetalleEnvase(String aufnr, CarrilesUbicacionDTO carrilesDTO, String user, String werks);

	public int insertProcesoContingencia_5(OrdenProduccionInputDTO ordenProduccionInput, String hu);

	public int limpiaPendientes(String vbeln);

	public int limpiaPendientesXUsuario(String vbeln, String user);

	public OrdenProduccionInputDTO obtieneDepaletizadora(OrdenProduccionInputDTO ordenProduccionInput);

	public OrdenProduccionInputDTO obtieneReservaUbicacionHU1(OrdenProduccionInputDTO ordenProduccionInput);

	public int reservaUbicacionHU1(OrdenProduccionInputDTO ordenProduccionInput);

	public int reservaUbicacionHU2(OrdenProduccionInputDTO ordenProduccionInput);

	public ResultDTO validaPickeoPrevioHU(OrdenProduccionInputDTO ordenProduccionInput, String hu);

	public ResultDTO validarEntregaPickin(OrdenProduccionInputDTO ordenProduccionInput);
}
