package com.bmore.prueba.web.repository.old;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmore.prueba.config.DBConnection;
import com.bmore.prueba.web.dto.ResultDTO;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class ZContingenciaRepository {

	private String DIR = "C:\\ZContingencia\\";

	private String TXT = "CentroZContingencia";

	private String DATAMART = "/Datamart/dataBCP/";

	private String EXT = ".txt";

	// private String DIRCMP = "C:\\dataUpload\\dataCmp\\";

	private String GET_SFTP_DATA = "SELECT IP, PORT, [USER], [PASSWORD], FOLDER FROM TB_BCPS_SFTP_DATAMART";

	private String ZCONTINGENCIA = "select CASE WHEN IDPROC IS NULL THEN 0 ELSE IDPROC END AS IDPROC, CONVERT(varchar(10), FECHA ,103) as FECHA, "
			+ " SUBSTRING(convert(varchar,HORA), 1, 8)  as HORA, " + " UPPER(CENTRO) as CENTRO, "
			+ " TRANSPORTE = Case when TRANSPORTE is null then '' else TRANSPORTE end, "
			+ " HU = Case when HU is null then '' else HU end, "
			+ " TIPO_ALMACEN = Case when TIPO_ALMACEN is null then '' else TIPO_ALMACEN end, "
			+ " DESTINO = Case when DESTINO is null then '' else rtrim(ltrim(DESTINO)) end, "
			+ " ENTREGA = Case when ENTREGA is null then '' else ENTREGA end,"
			+ " ORDEN_PRODUCCION = Case when ORDEN_PRODUCCION is null then '' else ORDEN_PRODUCCION end, "
			+ " CANTIDAD = Case when CANTIDAD is null then '' else CANTIDAD end, "
			+ " CONTROL_CALIDAD = Case when CONTROL_CALIDAD is null then '' else CONTROL_CALIDAD end, "
			+ " ALMACEN = Case when ALMACEN is null then '' else ALMACEN end, "
			+ " USUARIO = Case when USUARIO is null then '' else USUARIO end, "
			+ " TARIMA  = Case when TARIMA is null then '' else TARIMA end, "
			+ " UNIDADMEDIDA = Case when UNIDADMEDIDA is null then 'CJ' else UNIDADMEDIDA end, "
			+ " LOTE1 = Case when LOTE1 is null then '' else LOTE1 end, "
			+ " LOTE2 = Case when LOTE2 is null then '' else LOTE2 end, "
			+ " FOLINT = Case when FOLINT is null then '' else FOLINT end, "
			+ " FOLEXT = Case when FOLEXT is null then '' else FOLEXT end, "
			+ " SELLO = Case when SELLO is null then '' else SELLO end, "
			+ " UUID =  Case when UUID is null then '' else UUID end, "
			+ " NOCAJA = Case when NOCAJA is null then '' else NOCAJA end, "
			+ " TALONEMBARQUE = Case when TALONEMBARQUE is null then '' else TALONEMBARQUE end, "
			+ " OPERADOR = Case when OPERADOR is null then '' else OPERADOR end, "
			+ " NORMAEMBALAJE = Case when NORMAEMBALAJE is null then '' else NORMAEMBALAJE end, "
			+ " MATERIAL = Case when MATERIAL is null then '' else MATERIAL end, "
			+ " LIFNR = Case when LIFNR is null then '' else LIFNR end, "
			+ " SELLO_IMPORTADOR = Case when SELLO_IMPORTADOR is null then '' else SELLO_IMPORTADOR end "
			+ " FROM zContingencia WITH(NOLOCK) where CENTRO  = ?" + " ORDER BY FECHA,HORA";

	private String REVISION = "select count(CENTRO) as CENTRO from zContingencia where CENTRO = ?";

	private String SP_BCPS_ZCONTINGENCIA_DELTA = "SP_BCPS_ZCONTINGENCIA_DELTA ?,?,?,?";

	// private String SP_BCPS_BACKUP_ZCONTINGENCIA =
	// "sp_bcps_utils_backup_transactional_data ?,?";

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	public ResultDTO zContingencia(String usuario, String centro) {

		Calendar calendar = new GregorianCalendar();

		String timeStamp = new Timestamp(calendar.getTimeInMillis()) + "";

		String mytimeStamp = timeStamp.replaceAll(":", "_").replaceAll(" ", "").replaceAll("-", "_");

		ResultDTO retorno = new ResultDTO();
		int result = 0;

		Connection con = DBConnection.createConnection();
		PreparedStatement ZCon = null;
		PreparedStatement ZRev = null;

		ResultSet ZConrs = null;
		ResultSet ZRevrs = null;
		PrintWriter writer = null;
		CallableStatement cs = null;

		try {

			ZRev = con.prepareStatement(REVISION);
			ZRev.setString(1, centro);
			ZRevrs = ZRev.executeQuery();

			while (ZRevrs.next()) {

				if (ZRevrs.getInt("CENTRO") == 0) {

					retorno.setId(4);
					retorno.setMsg("No se han realizado movimientos en el centro " + centro);

				} else {

					File folder = new File(DIR);

					if (!folder.exists())
						folder.mkdirs();

					writer = new PrintWriter(new OutputStreamWriter(
							new BufferedOutputStream(new FileOutputStream(DIR + TXT + centro + mytimeStamp + EXT)),
							"UTF-8"));

					ZCon = con.prepareStatement(ZCONTINGENCIA);

					ZCon.setString(1, centro);

					ZConrs = ZCon.executeQuery();
					writer.append("").println();

					while (ZConrs.next()) {

						writer.append(ZConrs.getString("CENTRO")).append(",");
						writer.append(ZConrs.getString("IDPROC")).append(",");
						writer.append(ZConrs.getString("TRANSPORTE")).append(",");
						writer.append(ZConrs.getString("ENTREGA")).append(",");
						writer.append(ZConrs.getString("HU")).append(",");
						writer.append(ZConrs.getString("ORDEN_PRODUCCION")).append(",");
						writer.append(ZConrs.getString("ALMACEN")).append(",");
						writer.append(ZConrs.getString("TIPO_ALMACEN")).append(",");
						writer.append(ZConrs.getString("DESTINO")).append(",");
						writer.append(ZConrs.getString("CONTROL_CALIDAD")).append(",");
						writer.append(ZConrs.getString("MATERIAL")).append(",");
						writer.append(ZConrs.getString("CANTIDAD")).append(",");
						writer.append(ZConrs.getString("UNIDADMEDIDA")).append(",");
						writer.append(ZConrs.getString("TARIMA")).append(",");
						writer.append(ZConrs.getString("FECHA")).append(",");
						writer.append(ZConrs.getString("HORA")).append(",");
						writer.append(ZConrs.getString("USUARIO")).append(",");
						writer.append(ZConrs.getString("FOLINT")).append(",");
						writer.append(ZConrs.getString("FOLEXT")).append(",");
						writer.append(ZConrs.getString("UUID")).append(",");
						writer.append(ZConrs.getString("SELLO")).append(",");
						writer.append(ZConrs.getString("NOCAJA")).append(",");
						writer.append(ZConrs.getString("LOTE1")).append(",");
						writer.append(ZConrs.getString("LOTE2")).append(",");
						writer.append(ZConrs.getString("NORMAEMBALAJE")).append(",");
						writer.append(ZConrs.getString("TALONEMBARQUE")).append(",");
						writer.append(ZConrs.getString("OPERADOR")).append(",");
						writer.append(ZConrs.getString("LIFNR")).append(",");
						writer.append(ZConrs.getString("SELLO_IMPORTADOR")).println();

					}

					writer.flush();
					writer.close();

					cs = con.prepareCall(SP_BCPS_ZCONTINGENCIA_DELTA);

					cs.setString(1, usuario);
					cs.setString(2, centro);
					cs.setString(3, timeStamp);
					cs.registerOutParameter(4, java.sql.Types.INTEGER);
					cs.execute();

					result = cs.getInt(4);

					switch (result) {

					case 1:

						if (SFTPUP(con, centro, mytimeStamp) != 1) {

							LOCATION.error("Error en SFTP...");
							retorno.setId(3);
							retorno.setMsg("Error al subir archivo en SFTP");

						} else {

							String mensaje = "El archivo para el centro " + centro.toUpperCase()
									+ " se ha cargado correctamente";

							retorno.setId(1);
							retorno.setMsg(mensaje);
							// Respaldar Zcontingencia

							// CallableStatement csBackup = null;
							//
							// csBackup = con
							// .prepareCall(SP_BCPS_BACKUP_ZCONTINGENCIA);
							//
							// csBackup.setString(1, centro);
							// csBackup.registerOutParameter(2,
							// java.sql.Types.INTEGER);
							//
							// csBackup.execute();
							//
							// int csBackupResult = -1;
							//
							// csBackupResult = csBackup.getInt(2);
							//
							// if (csBackupResult == 1) {
							//
							// retorno.setId(1);
							// retorno.setMsg(mensaje);
							//
							// } else {
							//
							// retorno
							// .setMsg(mensaje
							// +
							// ". No se realizó respaldo de ZContingencia, solicitar respaldo manual."+
							// csBackupResult);
							// }

						}
						break;
					default:

						retorno.setId(2);
						retorno.setMsg("Error al crear el log de carga en ZContingencia");

					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace(System.out);
			retorno.setId(5);
			retorno.setMsg(e.getMessage());
		} catch (Exception en) {
			en.printStackTrace(System.out);
			retorno.setId(6);
			retorno.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}

		return retorno;
	}

	private int SFTPUP(Connection con, String werks, String mytimeStamp) {
		JSch jsch = new JSch();
		Session session = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		int retorno = 0;
		try {
			stm = con.prepareStatement(GET_SFTP_DATA);
			rs = stm.executeQuery();
			jsch.addIdentity("C:\\SFTP_KEY\\bcps_private.ppk");
			while (rs.next()) {

				session = jsch.getSession(rs.getString("USER"), rs.getString("IP"), rs.getInt("PORT"));

				// session.setPassword(rs.getString("PASSWORD"));

				session.setConfig("StrictHostKeyChecking", "no");
				session.setConfig("compression.s2c", "zlib@openssh.com,zlib,none");
				session.setConfig("compression.c2s", "zlib@openssh.com,zlib,none");
				session.connect();

				Channel channel = session.openChannel("sftp");
				channel.connect();

				ChannelSftp sftpChannel = (ChannelSftp) channel;

				sftpChannel.put(DIR + TXT + werks + mytimeStamp + EXT,
						DATAMART + rs.getString("FOLDER") + "/" + TXT + werks + EXT, ChannelSftp.OVERWRITE);

				sftpChannel.exit();
				session.disconnect();
				retorno = 1;
			}
		} catch (SQLException e) {
			e.printStackTrace(System.out);
			retorno = 2;
		} catch (JSchException e) {
			e.printStackTrace(System.out);
			retorno = 3;
		} catch (Exception e) {
			e.printStackTrace(System.out);
			retorno = 4;
		}

		return retorno;
	}

// TODO Remove unused code found by UCDetector
// 	public boolean dataCompare(Connection con, String werks, String mytimeStamp) {
// 
// 		boolean datCmp = false;
// 
// 		File data1 = new File(DIR + TXT + werks + mytimeStamp + EXT);
// 		File data2 = new File(DIRCMP + TXT + werks + EXT);
// 
// 		byte[] a = new byte[(int) data1.length()];
// 		byte[] b = new byte[(int) data2.length()];
// 
// 		if (a.length == b.length) {
// 
// 			datCmp = true;
// 
// 		} else {
// 
// 			datCmp = false;
// 
// 		}
// 
// 		return datCmp;
// 
// 	}

// TODO Remove unused code found by UCDetector
// 	public int SFTPDN(Connection con, String werks, String mytimeStamp) {
// 		JSch jsch = new JSch();
// 		Session session = null;
// 		PreparedStatement stm = null;
// 		ResultSet rs = null;
// 		int retorno = 0;
// 		try {
// 			jsch.addIdentity("C:\\SFTP_KEY\\bcps_private.ppk");
// 			stm = con.prepareStatement(GET_SFTP_DATA);
// 			rs = stm.executeQuery();
// 			while (rs.next()) {
// 				session = jsch.getSession(rs.getString("USER"), rs.getString("IP"), rs.getInt("PORT"));
// 				session.setConfig("StrictHostKeyChecking", "no");
// 				session.setConfig("compression.s2c", "zlib@openssh.com,zlib,none");
// 				session.setConfig("compression.c2s", "zlib@openssh.com,zlib,none");
// 				// session.setPassword(rs.getString("PASSWORD"));
// 				session.connect();
// 				Channel channel = session.openChannel("sftp");
// 				channel.connect();
// 				ChannelSftp sftpChannel = (ChannelSftp) channel;
// 				dirOption(1);
// 				sftpChannel.get(DATAMART + rs.getString("FOLDER") + "/" + TXT + werks + EXT,
// 						DIRCMP + TXT + werks + EXT);
// 
// 				if (dataCompare(con, werks, mytimeStamp)) {
// 
// 					retorno = 1;
// 
// 				} else {
// 
// 					retorno = 2;
// 
// 				}
// 
// 				sftpChannel.exit();
// 				session.disconnect();
// 				dirOption(2);
// 			}
// 		} catch (SQLException e) {
// 			e.printStackTrace(System.out);
// 			retorno = 4;
// 		} catch (JSchException e) {
// 			e.printStackTrace(System.out);
// 			retorno = 5;
// 		} catch (Exception e) {
// 			e.printStackTrace(System.out);
// 			retorno = 6;
// 		}
// 
// 		return retorno;
// 	}

// TODO Remove unused code found by UCDetector
//	private void dirOption(int opt) {
//		File folder = new File(DIRCMP);
//		switch (opt) {
//		case 1:
//
//			if (!folder.exists()) {
//				folder.mkdir();
//			}
//
//			break;
//
//		default:
//
//			try {
//				if (folder.isDirectory()) {
//					for (File arc : folder.listFiles()) {
//						arc.delete();
//					}
//				}
//				folder.delete();
//			} catch (Exception e) {
//				e.printStackTrace(System.out);
//			}
//
//			break;
//		}
//	}

}
