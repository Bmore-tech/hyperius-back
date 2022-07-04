package com.bmore.hyperius.web.repository;

import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.EmbarqueDTO;
import com.bmore.hyperius.web.dto.EntregaDetalleDTO;
import com.bmore.hyperius.web.dto.EntregaInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;

public interface IMEmbarquePTRepository {

	public ResultDTO confirmaHusEnCamionFurgon(EntregaInputDTO entregaInput, String hu);

	public ResultDTO consumeHUs(EntregaInputDTO entregaInput);

	public ResultDTO contabilizadoOK(String entry);

	public ResultDTO contabilizaEntrega(EmbarqueDTO embarqueDTO, String user);

	public EntregaDetalleDTO getDataHU(String hu, String werks, String lgtyp, String lgpla);

	public EmbarqueDTO getEmbarque(EmbarqueDTO embarqueDTOInput);

	public EmbarqueDTO getEmbarqueDetalle(EmbarqueDTO embarqueDTO);

	public CarrilesUbicacionDTO getStock(String werks, String matnr);

	public ResultDTO ingresaDetalleEnvase(String VBELN, CarrilesUbicacionDTO carrilesDTO, String user, String werks);

	public ResultDTO obtieneDescripcionMaterial(String matnr, String vblen);

	public ResultDTO obtieneEntregaDeTransporte(String tknum);

	public EntregaInputDTO obtieneReservaUbicacionHU1(EntregaInputDTO entregaInput);

	public ResultDTO reservaHus(EntregaInputDTO entregaInput);

	public ResultDTO validaPickeoPrevioHU(EntregaInputDTO entregaInput, String hu);

	public EntregaInputDTO validarEntregaPickin(EntregaInputDTO entregaInput);

	public EntregaInputDTO validarEntregaPickinCompleto(EntregaInputDTO entregaInput);
}
