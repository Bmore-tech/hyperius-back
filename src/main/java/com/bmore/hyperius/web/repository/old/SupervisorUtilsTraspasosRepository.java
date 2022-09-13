package com.bmore.hyperius.web.repository.old;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.config.DBConnection;
import com.bmore.hyperius.web.dto.AlmacenDTO;
import com.bmore.hyperius.web.dto.AlmacenItemDTO;
import com.bmore.hyperius.web.dto.AlmacenesDTO;
import com.bmore.hyperius.web.dto.InventarioDTO;
import com.bmore.hyperius.web.dto.InventarioDetalleDTO;
import com.bmore.hyperius.web.dto.InventarioDetalleDTOItem;
import com.bmore.hyperius.web.dto.ResultDTO;

@Repository
public class SupervisorUtilsTraspasosRepository {
  
  @Autowired
  private DBConnection dbConnection;

	private String LGORT = "select distinct(almacen) from centrosAlmacenesPermitidos where centro = ?";

	private String LGNUM = "select distinct (noAlmacen) from centrosAlmacenesPermitidos where centro= ? and almacen= ?";

	private String LGTYP = "select distinct (tipoAlmacen) from centrosAlmacenesPermitidos where centro = ? and almacen = ? and noAlmacen= ? order by tipoAlmacen";

	private String LGPLA = "select distinct(lgpla)  from LAGP where LGNUM = ? and LGTYP = ?";

	private String LQUA = "select LGNUM,LGTYP,LGPLA,MATNR,CHARG, VERME, LENUM from LQUA where LGNUM = ? and LGTYP = ? and LGPLA = ? and WERKS =?";

	private String LQUA_BY_CHARG = "select LGNUM,LGTYP,LGPLA,MATNR,CHARG, VERME, LENUM from LQUA where CHARG = ? and WERKS =?";

	private String TRASPASO = "SP_BCPS_WM_TRASPASOS ?, ?, ?, ?, ?, ? ";

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	public AlmacenesDTO lgortPermitidos(String werks) {

		ResultDTO resultDT = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmnt = null;
		ResultSet rs = null;

		AlmacenesDTO almacenesDTO = new AlmacenesDTO();
		AlmacenItemDTO almacenItemDTO = new AlmacenItemDTO();
		List<AlmacenDTO> items = new ArrayList<AlmacenDTO>();

		almacenesDTO.setResultDT(resultDT);
		almacenItemDTO.setItem(items);
		almacenesDTO.setItems(almacenItemDTO);
		try {

			stmnt = con.prepareStatement(LGORT);

			stmnt.setString(1, werks);
			rs = stmnt.executeQuery();

			while (rs.next()) {

				AlmacenDTO almacen = new AlmacenDTO();

				almacen.setLgort(rs.getString("almacen"));

				items.add(almacen);

			}

			if (items.size() > 0) {
				resultDT.setId(1);
				resultDT.setMsg("Almacén encontrado");
			} else {
				resultDT.setId(2);
				resultDT.setMsg("Almacén no encontrado");
			}

		} catch (SQLException e) {
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
		} catch (Exception en) {
			resultDT.setId(2);
			resultDT.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
			}
		}

		return almacenesDTO;

	}

	public AlmacenesDTO lgnumPermitidos(AlmacenDTO almacenDTO) {

		ResultDTO resultDT = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmnt = null;
		ResultSet rs = null;

		AlmacenesDTO almacenesDTO = new AlmacenesDTO();
		AlmacenItemDTO almacenItemDTO = new AlmacenItemDTO();
		List<AlmacenDTO> items = new ArrayList<AlmacenDTO>();

		almacenesDTO.setResultDT(resultDT);
		almacenItemDTO.setItem(items);
		almacenesDTO.setItems(almacenItemDTO);
		try {

			stmnt = con.prepareStatement(LGNUM);

			stmnt.setString(1, almacenDTO.getWerks());
			stmnt.setString(2, almacenDTO.getLgort());

			rs = stmnt.executeQuery();

			while (rs.next()) {

				AlmacenDTO almacen = new AlmacenDTO();

				almacen.setLgnum(rs.getString("noAlmacen"));

				items.add(almacen);

			}

			if (items.size() > 0) {
				LOCATION.error(">0");
				resultDT.setId(1);
				resultDT.setMsg("Numero de Almacén encontrado");

			} else {
				LOCATION.error("< 0 o = 0");
				resultDT.setId(2);
				resultDT.setMsg("Numero de Almacén no encontrado");

			}

		} catch (SQLException e) {
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
		} catch (Exception en) {
			resultDT.setId(2);
			resultDT.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
			}
		}

		return almacenesDTO;

	}

	public AlmacenesDTO lgtypPermitidos(AlmacenDTO almacenDTO) {

		ResultDTO resultDT = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmnt = null;
		ResultSet rs = null;

		AlmacenesDTO almacenesDTO = new AlmacenesDTO();
		AlmacenItemDTO almacenItemDTO = new AlmacenItemDTO();
		List<AlmacenDTO> items = new ArrayList<AlmacenDTO>();

		almacenesDTO.setResultDT(resultDT);
		almacenItemDTO.setItem(items);
		almacenesDTO.setItems(almacenItemDTO);
		try {

			stmnt = con.prepareStatement(LGTYP);

			stmnt.setString(1, almacenDTO.getWerks());
			stmnt.setString(2, almacenDTO.getLgort());
			stmnt.setString(3, almacenDTO.getLgnum());

			rs = stmnt.executeQuery();

			while (rs.next()) {

				AlmacenDTO almacen = new AlmacenDTO();

				almacen.setLgtyp(rs.getString("tipoAlmacen"));

				items.add(almacen);

			}

			if (items.size() > 0) {

				resultDT.setId(1);
				resultDT.setMsg("Tipos de Almacén encontrados");

			} else {

				resultDT.setId(2);
				resultDT.setMsg("Tipos de Almacén no encontrados");

			}

		} catch (SQLException e) {
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
		} catch (Exception en) {
			resultDT.setId(2);
			resultDT.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
			}
		}

		return almacenesDTO;

	}

	public AlmacenesDTO lgplaPermitidos(AlmacenDTO almacenDTO) {

		ResultDTO resultDT = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmnt = null;
		ResultSet rs = null;

		AlmacenesDTO almacenesDTO = new AlmacenesDTO();
		AlmacenItemDTO almacenItemDTO = new AlmacenItemDTO();
		List<AlmacenDTO> items = new ArrayList<AlmacenDTO>();

		almacenesDTO.setResultDT(resultDT);
		almacenItemDTO.setItem(items);
		almacenesDTO.setItems(almacenItemDTO);
		try {

			stmnt = con.prepareStatement(LGPLA);

			stmnt.setString(1, almacenDTO.getLgnum());
			stmnt.setString(2, almacenDTO.getLgtyp());

			rs = stmnt.executeQuery();

			while (rs.next()) {

				AlmacenDTO almacen = new AlmacenDTO();

				almacen.setLgpla(rs.getString("lgpla"));

				items.add(almacen);

			}

			if (items.size() > 0) {

				resultDT.setId(1);
				resultDT.setMsg("Ubicaciones encontradas");

			} else {

				resultDT.setId(2);
				resultDT.setMsg("No se encontraron ubicaciones");

			}

		} catch (SQLException e) {
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
		} catch (Exception en) {
			resultDT.setId(2);
			resultDT.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
			}
		}

		return almacenesDTO;

	}

	public InventarioDTO lquaBusquedaTraspasos(AlmacenDTO almacenDTO, int opc) {

		ResultDTO resultDT = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmnt = null;
		ResultSet rs = null;

		InventarioDTO inventarioDTO = new InventarioDTO();
		InventarioDetalleDTOItem inventarioDetalleDTOItem = new InventarioDetalleDTOItem();

		List<InventarioDetalleDTO> items = new ArrayList<InventarioDetalleDTO>();

		inventarioDetalleDTOItem.setItem(items);

		inventarioDTO.setItems(inventarioDetalleDTOItem);
		inventarioDTO.setResultDT(resultDT);

		try {

			switch (opc) {

			case 1:
				stmnt = con.prepareStatement(LQUA);

				stmnt.setString(1, almacenDTO.getLgnum());
				stmnt.setString(2, almacenDTO.getLgtyp());
				stmnt.setString(3, almacenDTO.getLgpla());
				stmnt.setString(4, almacenDTO.getWerks());
				break;

			case 2:
				stmnt = con.prepareStatement(LQUA_BY_CHARG);

				stmnt.setString(1, almacenDTO.getCharg());
				stmnt.setString(2, almacenDTO.getWerks());
				break;

			}

			rs = stmnt.executeQuery();

			while (rs.next()) {

				InventarioDetalleDTO inventarioDetalleDTO = new InventarioDetalleDTO();

				inventarioDetalleDTO.setLgnum(rs.getString("lgnum"));
				inventarioDetalleDTO.setLgtyp(rs.getString("lgtyp"));
				inventarioDetalleDTO.setLgpla(rs.getString("lgpla"));
				inventarioDetalleDTO.setMatnr(rs.getString("matnr"));
				inventarioDetalleDTO.setCharg(rs.getString("charg"));
				inventarioDetalleDTO.setVerme(rs.getString("verme"));
				inventarioDetalleDTO.setLenum(rs.getString("lenum"));

				items.add(inventarioDetalleDTO);

			}

			if (items.size() > 0) {

				resultDT.setId(1);
				resultDT.setMsg("Existe inventario en la ubicación");

			} else {

				resultDT.setId(2);
				resultDT.setMsg("No Existe inventario en la ubicación");

			}

		} catch (SQLException e) {
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
		} catch (Exception en) {
			resultDT.setId(2);
			resultDT.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
			}
		}

		return inventarioDTO;

	}

	public ResultDTO traspaso(InventarioDetalleDTO inventarioDetalleDTO,
			String user) {

		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();

		CallableStatement callableStatement = null;

		try {

			callableStatement = con.prepareCall(TRASPASO);

			LOCATION.error("HU: " + inventarioDetalleDTO.toString());

			callableStatement.setString(1, inventarioDetalleDTO.getLenum());
			callableStatement.setString(2, inventarioDetalleDTO.getLgnum());
			callableStatement.setString(3, inventarioDetalleDTO.getLgtyp());
			callableStatement.setString(4, inventarioDetalleDTO.getLgpla());
			callableStatement.setString(5, user);

			callableStatement.registerOutParameter(6, java.sql.Types.INTEGER);
			callableStatement.execute();

			int id = 0;
			id = callableStatement.getInt(6);
			result.setId(id);

		} catch (SQLException e) {
			result.setId(800);
			result.setMsg(LOCATION.getName() + "SQLException "
					+ e.toString());
			LOCATION.error(e.toString());
		} catch (Exception en) {
			result.setId(800);
			result.setMsg(en.getMessage());
			result.setMsg(LOCATION.getName() + "Exception "
					+ en.toString());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(800);
				result.setMsg(e.getMessage());
			}
		}
		return result;

	}
}
