package com.bmore.hyperius.web.repository.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.config.DBConnection;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.dto.TransportesDTO;

@Repository
public class TransportesRepository {
  
  @Autowired
  private DBConnection dbConnection;

	static String EXISTE_FIN_TRANSPORTE = "select tknum from HCMDB.dbo.VTTP WITH(NOLOCK) where tknum = ? ";
			//+ "and vbeln in (select vbeln from HCMDB.dbo.likp where KUNNR = (select kunnr from HCMDB.dbo.zCentrosBCPS where werks=?))"; se quita,sap no mando entregas

	static String EXISTE_INICIO_SALIDA_PT = "select distinct(tknum) from HCMDB.dbo.VTTP WITH(NOLOCK) where tknum =? ";//and vbeln in "
			//+ "(select vbeln from HCMDB.dbo.likp where VSTEL = (select vstel from HCMDB.dbo.zCentrosBCPS where werks=?))";

	static String STATUS_TRANSPORTE = "Select LFA.NAME1,LFA.NAME2 , LFA.STRAS, LFA.ORT01, LFA.PSTLZ, DPREG , UPREG "
			+ "from HCMDB.dbo.VTTK VTTK WITH(NOLOCK) inner join HCMDB.dbo.LFA1 LFA WITH(NOLOCK) on VTTK.TDLNR = LFA.LIFNR where STTRG = ? and TKNUM=?";

	static String UPDATE_TRANSPORTE = "update HCMDB.dbo.VTTK set STTRG = ? where TKNUM = ?";

	static String INSERT_PROCESO_ZCONTINGENCIA = "insert into HCMDB.dbo.zContingencia(IDPROC, FECHA, HORA, CENTRO, TRANSPORTE, USUARIO) "
			+ "values(?,convert(date,getdate()), convert(time,getdate()), ?,?,?)";

	public ResultDTO obtieneTransporte(String tknum, String werks, int idQuery) {
		ResultDTO result = new ResultDTO();
		Connection con = dbConnection.createConnection();
		PreparedStatement stmnt2 = null;
		ResultSet rs2 = null;
		try {
			if (idQuery == 1)
				stmnt2 = con.prepareStatement(EXISTE_FIN_TRANSPORTE);
			else
				stmnt2 = con.prepareStatement(EXISTE_INICIO_SALIDA_PT);

			stmnt2.setString(1, tknum);
			//stmnt2.setString(2, werks);
			rs2 = stmnt2.executeQuery();
			
			if (rs2.next()) {

				result.setId(1);
				result.setMsg("Transporte Encontrado");

			} else {
				result.setId(2);
				result.setMsg("Transporte NO Encontrado");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		return result;
	}

	public TransportesDTO getStatusTransporte(String tknum, String werks,
			String status) {

		ResultDTO result = new ResultDTO();
		TransportesDTO transporteDTO = new TransportesDTO();

		Connection con = dbConnection.createConnection();
		PreparedStatement stmnt2 = null;
		ResultSet rs2 = null;

		try {
			stmnt2 = con.prepareStatement(STATUS_TRANSPORTE);
			stmnt2.setString(1, status);
			stmnt2.setString(2, tknum);

			rs2 = stmnt2.executeQuery();
			if (rs2.next()) {
				String name2 = "";
				if (rs2.getString("NAME2") != null)
					name2 = rs2.getString("NAME2");

				transporteDTO.setTransporte(rs2.getString("NAME1") + " "
						+ name2 + ", " + rs2.getString("STRAS") + ", "
						+ rs2.getString("ORT01") + ", "
						+ rs2.getString("PSTLZ"));

				try {
					transporteDTO.setFechaPlaneada(rs2.getString("DPREG")
							.substring(0, 4)
							+ "-"
							+ rs2.getString("DPREG").substring(4, 6)
							+ "-"
							+ rs2.getString("DPREG").substring(6, 8)
							+ " "
							+

							rs2.getString("UPREG").substring(0, 2)
							+ ":"
							+ rs2.getString("UPREG").substring(2, 4));
				} catch (Exception e) {
					transporteDTO.setFechaPlaneada(rs2.getString("DPREG"));
				}

				result.setId(1);
				result.setMsg("Transporte Encontrado");

			} else {

				result.setId(2);
				result.setMsg("Transporte NO Encontrado");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		transporteDTO.setResultDT(result);
		return transporteDTO;
	}

	public ResultDTO updateTransporte(String tknum, String werks, String sttrg,
			String status) {

		ResultDTO result = new ResultDTO();

		Connection con = dbConnection.createConnection();
		PreparedStatement stmnt2 = null;
		int res2 = 0;

		try {

			stmnt2 = con.prepareStatement(UPDATE_TRANSPORTE);
			stmnt2.setString(1, sttrg);
			stmnt2.setString(2, tknum);

			res2 = stmnt2.executeUpdate();
			if (res2 == 1) {

				result.setId(1);

			} else {
				result.setId(2);
				result.setMsg("Error al actualizar status transporte.");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg("Error al actualizar status transporte: "
					+ e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg("Error al actualizar status transporte: "
					+ en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg("Error al actualizar status transporte: "
						+ e.getMessage());
			}
		}

		return result;
	}

	public ResultDTO insertProcesoContingenciaTransportes(String werks,
			String tknum, String user, String status) {

		ResultDTO result = new ResultDTO();

		Connection con = dbConnection.createConnection();
		PreparedStatement stmnt2 = null;
		int res2 = 0;

		try {

			stmnt2 = con.prepareStatement(INSERT_PROCESO_ZCONTINGENCIA);
			stmnt2.setString(1, status);
			stmnt2.setString(2, werks);
			stmnt2.setString(3, tknum);
			stmnt2.setString(4, user);

			res2 = stmnt2.executeUpdate();

			if (res2 == 1) {

				result.setId(1);
				result.setMsg("Entrada en tabla contingencia con exito");

			} else {
				result.setId(2);
				result.setMsg("Error al registrar Entrada carga transporte.");
			}
		} catch (SQLException e) {
			result.setId(2);
			result.setMsg("Error al registrar Entrada carga transporte: "
					+ e.getMessage());
		} catch (Exception en) {
			result.setId(2);
			result.setMsg("Error al registrar Entrada carga transporte: "
					+ en.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(2);
				result.setMsg("Error al registrar Entrada carga transporte: "
						+ e.getMessage());
			}
		}

		return result;

	}

}
