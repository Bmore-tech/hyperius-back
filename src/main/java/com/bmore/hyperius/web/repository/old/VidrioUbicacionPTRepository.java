package com.bmore.hyperius.web.repository.old;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmore.hyperius.config.DBConnection;
import com.bmore.hyperius.web.dto.OrdenProduccionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDetalleDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDetalleDTOItem;
import com.bmore.hyperius.web.dto.OrdenProduccionInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.utils.Utils;

public class VidrioUbicacionPTRepository {

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	private static String VALIDA_ORDEN_PRODUCCION = "select AUFK.AUFNR,  dbo.conFec(AUFK.ERDAT) as ERDAT, AUFK.WERKS, zCentrosBCPS.descripcion "
			+ "from HCMDB.dbo.AUFK AUFK inner join HCMDB.dbo.zCentrosBCPS zCentrosBCPS on AUFK.WERKS = zCentrosBCPS.werks "
			+ "and AUFK.AUFNR=? and AUFK.WERKS=?";

	private static String DATOS_ORDEN = "select POSNR,AFPO.MATNR,MAKT.MAKTX,PSMNG,WEMNG,MEINS from HCMDB.dbo.AFPO AFPO "
			+ "left join HCMDB.dbo.MAKT MAKT on AFPO.MATNR = MAKT.MATNR where AFPO.AUFNR=?";

	private static String SUMA_ZPICKING_VEKP_ORDEN_PRODUCCION = "select sum(cast(VEMNG as decimal(9,3)))as cantidad from HCMDB.dbo.vepo where MATNR=? and VELIN=1 "
			+ "and VENUM in(select VENUM  from HCMDB.dbo.VEKP where VPOBJKEY=? and LGNUM is not null)";

	private static String HUS_EN_ORDEN_PRODUCCION = "select count(*) as cantidad from VEPO where matnr=? and VENUM in "
			+ "(select venum from VEKP where VPOBJKEY=(select VPOBJKEY from AUFK where AUFNR = ?))";

	private static String VIDRIO_UBICA_PT = "exec sp_bcps_im_ubicacionPT_ingresa_hu ?,?,?,?,?,?";

	public OrdenProduccionDTO getOrden(OrdenProduccionDTO ordenInput) {

		OrdenProduccionDTO orden = new OrdenProduccionDTO();
		ResultDTO result = new ResultDTO();
		result.setId(0);

		Connection con = DBConnection.createConnection();

		PreparedStatement stmn = null;

		ResultSet rs = null;

		try {
			LOCATION
					.error("Dentro de DAO: " + ordenInput.getOrdenProduccion());
			LOCATION.error("Dentro de DAO: " + ordenInput.getWerks());

			stmn = con.prepareStatement(VALIDA_ORDEN_PRODUCCION);
			stmn.setString(1, ordenInput.getOrdenProduccion());
			stmn.setString(2, ordenInput.getWerks());
			rs = stmn.executeQuery();
			if (rs.next()) {

				orden.setOrdenProduccion(rs.getString("AUFNR"));
				orden.setFabrica(rs.getString("WERKS"));
				orden.setFechaDocumento(rs.getString("ERDAT"));
				orden.setFabricaDesc(rs.getString("descripcion"));

				result.setId(1);
				result.setMsg("Recuperacion de datos de cabecera correcta");

			} else {
				result.setId(2);
				result.setMsg("La orden de producción no existe");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		orden.setResultDT(result);
		return orden;
	}

	public OrdenProduccionDTO detalleOrdenProduccion(String aufnr, String werks) {
		OrdenProduccionDTO ordenProduccionDTO = new OrdenProduccionDTO();

		OrdenProduccionDetalleDTOItem detalleList = new OrdenProduccionDetalleDTOItem();
		List<OrdenProduccionDetalleDTO> detalle = new ArrayList<OrdenProduccionDetalleDTO>();

		ResultDTO result = new ResultDTO();
		Connection con = DBConnection.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;

		PreparedStatement stmn2 = null;
		ResultSet rs2 = null;

		try {
			stmn = con.prepareStatement(DATOS_ORDEN);
			stmn.setString(1, aufnr);

			rs = stmn.executeQuery();
			int cant = 0;

			while (rs.next()) {

				cant++;
				OrdenProduccionDetalleDTO item = new OrdenProduccionDetalleDTO();

				item.setPosicion(rs.getString("POSNR"));
				item.setMaterial(rs.getString("MATNR"));
				item.setDescripcion(rs.getString("MAKTX"));
				item.setCajas(rs.getString("PSMNG"));
				item.setCajasAsignadas(rs.getString("WEMNG"));
				item.setMe(rs.getString("MEINS"));

				try {

					stmn2 = con
							.prepareStatement(SUMA_ZPICKING_VEKP_ORDEN_PRODUCCION);

					stmn2.setString(1, item.getMaterial());
					stmn2.setString(2, aufnr);

					rs2 = stmn2.executeQuery();

					if (rs2.next()) {

						String cantidad = rs2.getString("cantidad");

						LOCATION.error("Cantidad :@  " + cantidad
								+ "     WEMNG  " + rs.getString("WEMNG"));

						BigDecimal numero1 = new BigDecimal(cantidad.trim());
						BigDecimal numero2 = new BigDecimal(rs
								.getString("WEMNG").trim());

						LOCATION.error("antes de suma: "+numero1);
						
						item.setCajasAsignadas(numero1.add(numero2).setScale(3,
								RoundingMode.HALF_UP)
								+ "");
						
						LOCATION.error("depsues de suma:");

					}

				} catch (Exception e) {
					LOCATION.error("Error en SUMA_ZPICKING_ORDEN_PRODUCCION @: "
							+ e);
				}

				try {

					stmn2 = con.prepareStatement(HUS_EN_ORDEN_PRODUCCION);

					stmn2.setString(1, item.getMaterial());
					stmn2.setString(2, aufnr);

					rs2 = stmn2.executeQuery();

					if (rs2.next()) {
						String cantidad = rs2.getString("cantidad");
						item.setHus(cantidad);
					}

				} catch (Exception e) {
					LOCATION.error("Error en HUS_EN_ORDEN_PRODUCCION: " + e);
				}

				detalle.add(item);

			}

			if (cant != 0) {
				result.setId(1);
				result.setMsg("Detalle de Orden recuperado con exito");
			} else {
				result.setId(2);
				result
						.setMsg("No fue posible recuperar el detalle de la orden");
			}

		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		detalleList.setItem(detalle);
		ordenProduccionDTO.setItems(detalleList);

		ordenProduccionDTO.setResultDT(result);
		return ordenProduccionDTO;
	}

	public ResultDTO vidrioUbicaPT(OrdenProduccionInputDTO ordenProduccion) {

		ResultDTO result = new ResultDTO();
		result.setId(0);

		Connection con = DBConnection.createConnection();

		CallableStatement callableStatement = null;

		try {

			// exec sp_vidrio_ubicacionPT @EXIDV, @WERKS, @MATNR, @LGORT,
			// @AUFNR, @USER, @RESULT

			callableStatement = con.prepareCall(VIDRIO_UBICA_PT);

			callableStatement.setString(1, ordenProduccion.getHu1());
			callableStatement.setString(2, ordenProduccion.getWerks());
			callableStatement.setString(3, ordenProduccion.getOrdeProduccion());
			callableStatement.setString(4, ordenProduccion
					.getUsuarioMontacarga());
			callableStatement.setString(5, Utils.getKeyTimeStamp());

			callableStatement.registerOutParameter(6, java.sql.Types.INTEGER);
			callableStatement.execute();

			int id = 0;
			id = callableStatement.getInt(6);

			result.setId(id);

		} catch (SQLException e) {
			result.setId(200);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(200);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(200);
				result.setMsg(e.getMessage());
			}
		}

		return result;

	}
}
