package com.bmore.hyperius.web.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.dto.TransportesDTO;
import com.bmore.hyperius.web.repository.TransportesRepository;

public class TransportesRepositoryImpl implements TransportesRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  static String EXISTE_FIN_TRANSPORTE = "select tknum from HCMDB.dbo.VTTP WITH(NOLOCK) where tknum = ? ";
  // + "and vbeln in (select vbeln from HCMDB.dbo.likp where KUNNR = (select kunnr
  // from HCMDB.dbo.zCentrosBCPS where werks=?))"; se quita,sap no mando entregas

  static String EXISTE_INICIO_SALIDA_PT = "select distinct(tknum) from HCMDB.dbo.VTTP WITH(NOLOCK) where tknum =? ";// and
                                                                                                                    // vbeln
                                                                                                                    // in
                                                                                                                    // "
  // + "(select vbeln from HCMDB.dbo.likp where VSTEL = (select vstel from
  // HCMDB.dbo.zCentrosBCPS where werks=?))";

  static String STATUS_TRANSPORTE = "Select LFA.NAME1,LFA.NAME2 , LFA.STRAS, LFA.ORT01, LFA.PSTLZ, DPREG , UPREG "
      + "from HCMDB.dbo.VTTK VTTK WITH(NOLOCK) inner join HCMDB.dbo.LFA1 LFA WITH(NOLOCK) on VTTK.TDLNR = LFA.LIFNR where STTRG = ? and TKNUM=?";

  static String UPDATE_TRANSPORTE = "update HCMDB.dbo.VTTK set STTRG = ? where TKNUM = ?";

  static String INSERT_PROCESO_ZCONTINGENCIA = "insert into HCMDB.dbo.zContingencia(IDPROC, FECHA, HORA, CENTRO, TRANSPORTE, USUARIO) "
      + "values(?,convert(date,getdate()), convert(time,getdate()), ?,?,?)";

  @Override
  public ResultDTO obtieneTransporte(String tknum, String werks, int idQuery) {
    ResultDTO result = new ResultDTO();
    String query = "";
    Object[] args = { tknum };

    if (idQuery == 1)
      query = EXISTE_FIN_TRANSPORTE; // + "and vbeln in (select vbeln from HCMDB.dbo.likp where KUNNR = (select kunnr
                                     // from HCMDB.dbo.zCentrosBCPS where werks=?))"; se quita,sap no mando entregas

    else
      query = EXISTE_INICIO_SALIDA_PT; // and vbeln in "
    // + "(select vbeln from HCMDB.dbo.likp where VSTEL = (select vstel from
    // HCMDB.dbo.zCentrosBCPS where werks=?))";
    // stmnt2.setString(2, werks);

    jdbcTemplate.query(query, args, new RowMapper<ResultDTO>() {
      @Override
      public ResultDTO mapRow(ResultSet rs, int i) throws SQLException {
        if (rs.next()) {
          result.setId(1);
          result.setMsg("Transporte Encontrado");
        } else {
          result.setId(2);
          result.setMsg("Transporte NO Encontrado");
        }
        return result;
      }
    });
    return result;
  }

  @Override
  public TransportesDTO getStatusTransporte(String tknum, String werks,
      String status) {

    Object[] args = { status, tknum };
    TransportesDTO transporteDTO = new TransportesDTO();
    ResultDTO result = new ResultDTO();

    jdbcTemplate.query(STATUS_TRANSPORTE, args, new RowMapper<TransportesDTO>() {
      @Override
      public TransportesDTO mapRow(ResultSet rs2, int i) throws SQLException {

        TransportesDTO transporteDTO = new TransportesDTO();

        if (rs2.next()) {
          String name2 = "";
          if (rs2.getString("NAME2") != null)
            name2 = rs2.getString("NAME2");

          transporteDTO.setTransporte(rs2.getString("NAME1") + " "
              + name2 + ", " + rs2.getString("STRAS") + ", "
              + rs2.getString("ORT01") + ", "
              + rs2.getString("PSTLZ"));

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

          result.setId(1);
          result.setMsg("Transporte Encontrado");

        } else {

          result.setId(2);
          result.setMsg("Transporte NO Encontrado");
        }
        return transporteDTO;
      }
    });

    transporteDTO.setResultDT(result);
    return transporteDTO;
  }

  @Override
  public ResultDTO updateTransporte(String tknum, String werks, String sttrg,
      String status) {
    ResultDTO result = new ResultDTO();
    Object[] args = { sttrg, tknum };
    int res2 = this.jdbcTemplate.update(UPDATE_TRANSPORTE, args, tknum);

    if (res2 == 1) {

      result.setId(1);

    } else {
      result.setId(2);
      result.setMsg("Error al actualizar status transporte.");
    }

    return result;
  }

  @Override
  public ResultDTO insertProcesoContingenciaTransportes(String werks,
      String tknum, String user, String status) {

    ResultDTO result = new ResultDTO();
    Object[] args = { status, werks, tknum, user };

    int res2 = jdbcTemplate.update(INSERT_PROCESO_ZCONTINGENCIA, args);

    if (res2 == 1) {
      result.setId(1);
      result.setMsg("Entrada en tabla contingencia con exito");

    } else {
      result.setId(2);
      result.setMsg("Error al registrar Entrada carga transporte.");
    }
    return result;
  }

}
