package com.bmore.hyperius.web.repository;

import java.util.HashMap;

import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.EmbarqueDTO;
import com.bmore.hyperius.web.dto.EmbarqueDetalleDTO;
import com.bmore.hyperius.web.dto.EntregaDetalleDTO;
import com.bmore.hyperius.web.dto.EntregaInputDTO;
import com.bmore.hyperius.web.dto.HuDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.utils.export.ExportacionDatasource;
import com.bmore.hyperius.web.utils.remission.RemisionDatasource;

public interface EmbarquePTRepository {

	public ResultDTO cambiarCantidadOrdenProduccion(EmbarqueDetalleDTO embarqueDetalleDTO, String user, String werks);

	public ResultDTO confirmaHusEnCamionFurgon(EntregaInputDTO entregaInput);

	public ResultDTO contabilizadoOK(String entry);

	public ResultDTO contabilizaEntrega(EmbarqueDTO embarqueDTO, String user);

	public ResultDTO contabilizaEntregaExport(EmbarqueDTO embarqueDTO, String user);

	public CarrilesUbicacionDTO getCarriles(String WERKS, String MATNR, String ID_PR, String ID_PR_Z,
			HashMap<String, String> carrilesBloqueados, HashMap<String, String> carrilesMaterialBloqueado);

	public HashMap<String, String> getCarrilesBloqueados(String idProceso, String werks);

	public HashMap<String, String> getCarrilesMaterialBloqueado(String matnr, String werks);

	public EntregaDetalleDTO getDataHU(String hu, String werks, String lgtyp, String lgpla);

	public EmbarqueDTO getEmbarque(EmbarqueDTO embarqueDTOInput);

	public EmbarqueDTO getEmbarqueDetalle(EmbarqueDTO embarque);

	public ResultDTO ingresaDetalleEnvase(String VBELN, CarrilesUbicacionDTO carrilesDTO, String user, String werks);

	public int isRstAllowed(HuDTO huDTO);

	public ResultDTO limpiaPendientes(String vbeln);

	public ResultDTO limpiaPendientesXUsuario(String vbeln, String user);

	public String obtenerCliente(String noEntrega, String num);

	public String obtenerDatosCliente(String noEntrega);

	public String obtenerDirPlanta(String noEntrega);

	public String obtenerFecha();

	public String obtenerPlanta(String noEntrega);

	public RemisionDatasource obtenerTabla(String noEntrega);

	public ExportacionDatasource obtenerTablaExp(String noEntrega);

	public EntregaInputDTO reservaUbicaciones(EntregaInputDTO entregaInput);

	public ResultDTO validaPickeoPrevioHU(EntregaInputDTO entregaInput, String hu);

	public EntregaInputDTO validarEntregaPickin(EntregaInputDTO entregaInput);

	public EntregaInputDTO validarEntregaPickinCompleto(EntregaInputDTO entregaInput);

}
