package com.bmore.hyperius.web.service;

import com.bmore.hyperius.web.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.web.dto.CarrilesBloqueadosDTO;
import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.EmbarqueDTO;
import com.bmore.hyperius.web.dto.EntregasTransportesDTO;
import com.bmore.hyperius.web.dto.HUsEnTransporteDetalleDTO;
import com.bmore.hyperius.web.dto.HusEnTransporteDTO;
import com.bmore.hyperius.web.dto.InventarioDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.dto.TablasSqlDTO;
import com.bmore.hyperius.web.dto.UsuarioDTO;
import com.bmore.hyperius.web.dto.UsuarioItemDTO;

/**
 * Interface para realizar las operaciones de negocio de Supervisor Utils.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 04-08-2020
 */
public interface SupervisorUtilsService {

	public UsuarioItemDTO buscarUsuario(String user);

	public TablasSqlDTO cargaBCPS(String werks, String user);

	public ResultDTO cargaSAP(String werks, String user);

	public ResultDTO crearUsuario(UsuarioDTO user, String werksAdmin);

	public ResultDTO desbloquearCarril(CarrilesUbicacionDTO carriles, String werks);

	public ResultDTO eliminarUsuario(UsuarioDTO user, String werksAdmin);

	public ResultDTO liberarHusEnTransporte(CarrilesUbicacionDTO carriles, String werks);

	public ResultDTO modificarUsuario(UsuarioDTO user, String werksAdmin);

	public CarrilesBloqueadosDTO obtieneCarrilBloqueado(UsuarioDTO usuario);

	public HusEnTransporteDTO obtieneCarrilesBloqueados(HUsEnTransporteDetalleDTO husEnTransporteDetalleDTO,
			String werks);

	public EntregasTransportesDTO obtieneEntrega(String tknum, String werks);

	public EntregasTransportesDTO obtieneEntregas(String werks);

	public EntregasTransportesDTO obtieneEntregasAgencias(UsuarioDTO usuario);

	public EmbarqueDTO obtieneEntregasAgenciasDetalle(String vbeln);

	public HusEnTransporteDTO obtieneHusBloqueados(HUsEnTransporteDetalleDTO husEnTransporteDetalleDTO, String werks);

	public InventarioDTO obtieneInventario(String werks, String opc);

	public ResultDTO switchUbsBCPS(String werks, String password);

	public ResultDTO switchUbsSAP(String werks, String password);

	public ResultDTO validaCarril(CarrilUbicacionDTO carril);
}
