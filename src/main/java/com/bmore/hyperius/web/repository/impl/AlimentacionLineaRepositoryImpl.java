package com.bmore.hyperius.web.repository.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.web.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDetalleDTO;
import com.bmore.hyperius.web.dto.OrdenProduccionDetalleDTOItem;
import com.bmore.hyperius.web.dto.OrdenProduccionInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.AlimentacionLineaRepository;

@Repository
public class AlimentacionLineaRepositoryImpl implements AlimentacionLineaRepository {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public Integer confirmaHUenDepa(OrdenProduccionInputDTO ordenProduccionInput, String hus) {
    String query = "UPDATE TOP (1) ZPickingEntregaEntrante SET Status = '2', EXIDV = ? "
        + "WHERE usuarioMontacarga = ?  AND Status = '1' AND EXIDV IS NULL AND VBELN = ? AND idProceso = '2'";
    Object[] args = { hus, ordenProduccionInput.getUsuarioMontacarga(), ordenProduccionInput.getOrdeProduccion() };

    return jdbcTemplate.update(query, args);
  }

  @Override
  public Integer consumeInventario(String hu, OrdenProduccionInputDTO ordenProduccionInput) {
    String query = "UPDATE LQUA SET SKZUA = 'X' WHERE LENUM = ?";

    return jdbcTemplate.update(query, hu);
  }

  @Override
  public OrdenProduccionDTO detalleOrdenProduccion(String aufnr, String werks) {
    String query1 = "SELECT distinct(RESB.RSPOS),RESB.MATNR,MAKT.MAKTX,RESB.BDMNG,RESB.ENMNG,RESB.MEINS "
        + "FROM HCMDB.dbo.RESB RESB WITH(NOLOCK) INNER JOIN HCMDB.dbo.MAKT MAKT WITH(NOLOCK) "
        + "ON RESB.MATNR = MAKT.MATNR " + "WHERE ( MAKT.MAKTX LIKE 'BOTE%' OR "
        + "MAKT.MAKTX LIKE 'BARRIL%' OR " + "MAKT.MAKTX LIKE 'ENV%' ) " + "AND RESB.AUFNR = ?";
    Object[] args1 = { aufnr };

    HashMap<String, String> map = new HashMap<String, String>();

    OrdenProduccionDTO ordenProduccionDTO = new OrdenProduccionDTO();
    OrdenProduccionDetalleDTOItem detalleList = new OrdenProduccionDetalleDTOItem();
    List<CarrilUbicacionDTO> carriles = new ArrayList<CarrilUbicacionDTO>();
    CarrilesUbicacionDTO carrilesList = new CarrilesUbicacionDTO();
    List<OrdenProduccionDetalleDTO> detalle = new ArrayList<OrdenProduccionDetalleDTO>();
    ResultDTO result = new ResultDTO();
    int contabilizado = 0;

    detalle = jdbcTemplate.query(query1, new RowMapper<OrdenProduccionDetalleDTO>() {

      @Override
      public OrdenProduccionDetalleDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrdenProduccionDetalleDTO item = new OrdenProduccionDetalleDTO();
        int cant = 0;
        String ENMNG = rs.getString("ENMNG");

        if (map.get(rs.getString("MATNR")) == null) {
          map.put(rs.getString("MATNR"), rs.getString("MATNR"));

          cant++;

          item.setPosicion(rs.getString("RSPOS"));
          item.setMaterial(rs.getString("MATNR"));
          item.setDescripcion(rs.getString("MAKTX"));
          item.setMe(rs.getString("MEINS"));

          String query2 = "SELECT RESB.MATNR, sum(convert(DECIMAL(18, 3), BDMNG)) as cantidadOriginal, sum(convert(DECIMAL(18, 3), ENMNG))as cantidadAsignada	"
              + "FROM HCMDB.dbo.RESB RESB WITH(NOLOCK) where MATNR = ? and "
              + "RESB.AUFNR = ? group by RESB.MATNR";
          Object[] args2 = { item.getMaterial(), aufnr };

          jdbcTemplate.queryForObject(query2, new RowMapper<OrdenProduccionDTO>() {

            @Override
            public OrdenProduccionDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
              item.setCajas(rs.getString("cantidadOriginal"));
              item.setCajasAsignadas(rs.getString("cantidadAsignada"));

              return null;
            }
          }, args2);

          String query3 = "select sum(convert(DECIMAL(18, 3), VERME))as cantidad from HCMDB.dbo.LQUA WITH(NOLOCK) where LENUM "
              + "in(SELECT EXIDV FROM HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where vbeln =? and matnr=? and "
              + "status='2' and EXIDV is not null and idProceso='2')";
          Object[] args3 = { aufnr, item.getMaterial() };

          jdbcTemplate.queryForObject(query3, new RowMapper<String>() {

            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
              String cantidad = rs.getString("cantidad");
              Float ENMNG_f = Float.parseFloat(ENMNG);
              Float cantidad_f = Float.parseFloat(cantidad);
              Float cajas = cantidad_f + ENMNG_f;

              item.setCajasAsignadas(cajas + "");

              return null;
            }
          }, args3);
        }

        if (cant != 0) {
          result.setId(1);
          result.setMsg("Detalle de Orden recuperado con exito");
        } else {
          result.setId(2);
          result.setMsg("No fue posible recuperar el detalle de la orden");
        }

        return item;
      }
    }, args1);

    if (contabilizado == detalle.size()) {
      ordenProduccionDTO.setContabilizar("true");
    } else {
      ordenProduccionDTO.setContabilizar("false");
    }

    detalleList.setItem(detalle);
    ordenProduccionDTO.setItems(detalleList);
    carrilesList.setItem(carriles);
    ordenProduccionDTO.setCarriles(carrilesList);
    ordenProduccionDTO.setResultDT(result);

    return ordenProduccionDTO;
  }

  @Override
  public CarrilesUbicacionDTO getCarriles(String werks, String matnr, String idPr, String ipPrZ,
      HashMap<String, String> carrilesBloqueados, HashMap<String, String> carrilesMaterialBloqueado) {
    String query1 = "exec sp_bcps_get_carriles_por_material ?,?,?,?,?";
    Object[] args1 = { matnr, werks, idPr, ipPrZ, 2 };

    String query2 = "select count(*) from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK)"
        + "where LGNUM = ? and LGTYP = ? and LGPLA = ? and idProceso='2' and (Status is null or Status ='1')  ";

    CarrilesUbicacionDTO carrilesDTO = new CarrilesUbicacionDTO();
    List<CarrilUbicacionDTO> carrilList = new ArrayList<CarrilUbicacionDTO>();

    jdbcTemplate.update(query1, args1, new RowMapper<String>() {

      @Override
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        while (rs.next()) {
          if (carrilesBloqueados.get(
              rs.getString("LGNUM") + rs.getString("LGTYP") + rs.getString("LGPLA").trim()) == null) {
            CarrilUbicacionDTO item = new CarrilUbicacionDTO();

            if (carrilesMaterialBloqueado.get(rs.getString("LGNUM") + rs.getString("LGTYP")
                + rs.getString("LGPLA").trim()) == null) {
              item.setStatusS("0");
            } else {
              item.setStatusS("1");
            }

            item.setLgnum(rs.getString("LGNUM"));
            item.setLgtyp(rs.getString("LGTYP"));
            item.setLgpla(rs.getString("LGPLA"));
            item.setCantidadHus(rs.getString("TOTAL"));
            item.setMe(rs.getString("MEINS"));
            item.setCajas(rs.getString("VERME"));

            Object[] args2 = { item.getLgnum(), item.getLgtyp(), item.getLgpla() };

            Integer cantidad = jdbcTemplate.queryForObject(query2, Integer.class, args2);

            item.setHusPendientes(cantidad.toString());
            carrilList.add(item);
          }
        }

        return null;
      }
    });
    carrilesDTO.setItem(carrilList);

    return carrilesDTO;
  }

  @Override
  public HashMap<String, String> getCarrilesBloqueados(String idProceso, String werks) {
    String query = "select distinct(CARRIL) "
        + "FROM ZPickingEntregaEntrante WITH(NOLOCK) WHERE idProceso!= ? AND werks = ? and (Status  is null or Status ='1')";
    Object[] args = { idProceso, werks };
    HashMap<String, String> map = new HashMap<String, String>();

    jdbcTemplate.query(query, new RowMapper<String>() {

      @Override
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        map.put(rs.getString("CARRIL").trim(), "");
        return null;
      }
    }, args);

    return map;
  }

  @Override
  public HashMap<String, String> getCarrilesMaterialBloqueado(String matnr, String werks) {
    String query = "select distinct LGNUM, LGTYP, LGPLA from LQUA with(nolock) where "
        + "MATNR=? and (BESTQ='S' or BESTQ ='Q') and WERKS=?";
    Object[] agrs = { matnr, werks };
    HashMap<String, String> map = new HashMap<String, String>();

    jdbcTemplate.query(query, new RowMapper<String>() {

      @Override
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        map.put(rs.getString("LGNUM") + rs.getString("LGTYP") + rs.getString("LGPLA").trim(), "");

        return null;
      }
    }, agrs);

    return map;
  }

  @Override
  public OrdenProduccionDetalleDTO getDataHU(String hu, String werks, String lgtyp, String lgpla) {
    String query1 = "SELECT LQUA.MATNR AS matnr FROM HCMDB.dbo.LQUA LQUA WITH(NOLOCK) "
        + "WHERE LENUM =  ? AND WERKS = ? AND LGTYP = ? AND LGPLA = ?";
    Object[] args1 = { hu, werks, lgtyp, lgpla };

    String query2 = "SELECT LQUA.MATNR AS matnr, LQUA.VERME AS vemng, LQUA.MEINS AS meins, MAKT.MAKTX AS maktx,BESTQ AS BESTQ "
        + " FROM LQUA LQUA WITH(NOLOCK) INNER JOIN MAKT MAKT WITH(NOLOCK) ON LQUA.MATNR = MAKT.MATNR "
        + " WHERE LENUM = ? AND WERKS = ? AND LGTYP = ? AND LGPLA = ?";

    ResultDTO result = new ResultDTO();
    OrdenProduccionDetalleDTO orden = new OrdenProduccionDetalleDTO();

    jdbcTemplate.query(query1, new RowMapper<OrdenProduccionDetalleDTO>() {

      @Override
      public OrdenProduccionDetalleDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        Object[] args2 = { hu, werks, lgtyp, lgpla };

        if (rs.next()) {
          jdbcTemplate.query(query2, new RowMapper<OrdenProduccionDetalleDTO>() {

            @Override
            public OrdenProduccionDetalleDTO mapRow(ResultSet rs2, int rowNum) throws SQLException {
              if (rs2.next()) {
                result.setId(1);
                result.setMsg("Material encontrado");

                orden.setMaterial(rs2.getString("matnr"));
                orden.setDescripcion(rs2.getString("maktx"));
                orden.setCajas(rs2.getString("vemng"));
                orden.setMe(rs2.getString("meins"));
                orden.setBestq(rs2.getString("bestq"));
              }
              return orden;
            }
          }, args2);
        }

        return orden;
      }
    }, args1);
    orden.setResultDT(result);

    return orden;
  }

  @Override
  public OrdenProduccionDTO getOrden(OrdenProduccionDTO ordenInput) {
    String query = "select AUFK.AUFNR,  dbo.conFec(AUFK.ERDAT) as ERDAT, AUFK.WERKS, zCentrosBCPS.descripcion "
        + "from HCMDB.dbo.AUFK AUFK WITH(NOLOCK) inner join HCMDB.dbo.zCentrosBCPS zCentrosBCPS WITH(NOLOCK) on AUFK.WERKS = zCentrosBCPS.werks "
        + "and AUFK.AUFNR=? and AUFK.WERKS=?";
    Object[] args = { ordenInput.getOrdenProduccion(), ordenInput.getWerks() };

    ResultDTO result = new ResultDTO();
    result.setId(2);
    result.setMsg("Orden de producción no existe");

    OrdenProduccionDTO orden = new OrdenProduccionDTO();

    orden = jdbcTemplate.queryForObject(query, new RowMapper<OrdenProduccionDTO>() {

      @Override
      public OrdenProduccionDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrdenProduccionDTO orden = new OrdenProduccionDTO();

        orden.setOrdenProduccion(rs.getString("AUFNR"));
        orden.setFabrica(rs.getString("WERKS"));
        orden.setFechaDocumento(rs.getString("ERDAT"));
        orden.setFabricaDesc(rs.getString("descripcion"));

        result.setId(1);
        result.setMsg("Recuperacion de Datos de cabecera correcta");

        logger.info("orden: " + orden.toString());

        return orden;
      }
    }, args);

    orden.setResultDT(result);

    return orden;
  }

  @Override
  public ResultDTO ingresaDetalleEnvase(String aufnr, CarrilesUbicacionDTO carrilesDTO, String user, String werks) {
    String query = "INSERT INTO HCMDB.dbo.ZPickingEntregaEntrante"
        + "(VBELN,LGNUM,LGTYP,LGPLA,MATNR,marcaTiempo,usuarioSupervisor,idProceso,werks,carril) values(?,?,?,?,?,getdate(),?,'2',?,?)";

    ResultDTO resultDT = new ResultDTO();

    for (int x = 0; x < carrilesDTO.getItem().size(); x++) {
      BigDecimal bigDecimal = new BigDecimal(carrilesDTO.getItem().get(x).getAsignarHus().trim());
      int ingresarZpicking = Integer.parseInt(bigDecimal.toBigInteger() + "");

      jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
          ps.setString(1, aufnr);
          ps.setString(2, carrilesDTO.getItem().get(i).getLgnum());
          ps.setString(3, carrilesDTO.getItem().get(i).getLgtyp());
          ps.setString(4, carrilesDTO.getItem().get(i).getLgpla());
          ps.setString(5, carrilesDTO.getItem().get(i).getMaterial());

          ps.setString(6, user);
          ps.setString(7, werks);
          ps.setString(8, carrilesDTO.getItem().get(i).getLgnum() + carrilesDTO.getItem().get(i).getLgtyp()
              + carrilesDTO.getItem().get(i).getLgpla());
        }

        @Override
        public int getBatchSize() {
          return ingresarZpicking;
        }
      });
    }

    resultDT.setId(1);
    resultDT.setMsg("Se registro la orden de producción correctamete, mandar a montacarga");

    return resultDT;
  }

  @Override
  public int insertProcesoContingencia_5(OrdenProduccionInputDTO ordenProduccionInput, String hu) {
    String query = " INSERT INTO zContingencia(IDPROC, FECHA, HORA, CENTRO, HU, ORDEN_PRODUCCION, CONTROL_CALIDAD, ALMACEN,USUARIO) "
        + " SELECT IDPROC = 5, convert(date, getdate()), convert(time, getdate()), "
        + " WERKS = ?, HU = ?, ORDEN_PRODUCCION = ?, CONTROL_CALIDAD = (SELECT top(1) BESTQ FROM LQUA WITH(NOLOCK) WHERE LENUM = ? AND WERKS = ?), "
        + " ALMACEN = ?, USUARIO = ?";
    Object[] args = { ordenProduccionInput.getWerks(), hu, ordenProduccionInput.getOrdeProduccion(), hu,
        ordenProduccionInput.getWerks(), ordenProduccionInput.getLgort(),
        ordenProduccionInput.getUsuarioMontacarga() };

    return jdbcTemplate.update(query, args);
  }

  @Override
  public int limpiaPendientes(String vbeln) {
    String query = "update HCMDB.dbo.ZPickingEntregaEntrante set status = null, usuarioMontacarga = null  "
        + " where VBELN = ? and Status = '1' and idProceso='2'";
    Object[] args = { vbeln };

    return jdbcTemplate.update(query, args);
  }

  @Override
  public int limpiaPendientesXUsuario(String vbeln, String user) {
    String query = "UPDATE ZPickingEntregaEntrante SET status = null, usuarioMontacarga = null  "
        + "WHERE VBELN = ? AND Status = '1' AND usuarioMontacarga = ? AND idProceso = '2'";
    Object[] args = { vbeln, user };

    return jdbcTemplate.update(query, args);
  }

  @Override
  public OrdenProduccionInputDTO obtieneDepaletizadora(OrdenProduccionInputDTO ordenProduccionInput) {
    String query = "SELECT LGTYP, LGPLA FROM RESB WITH(NOLOCK) WHERE MATNR = ? AND AUFNR = ?";
    Object[] args = { ordenProduccionInput.getMatnr(), ordenProduccionInput.getOrdeProduccion() };
    OrdenProduccionInputDTO ordenProduccion = new OrdenProduccionInputDTO();
    ResultDTO resultDT = new ResultDTO();

    return jdbcTemplate.queryForObject(query, new RowMapper<OrdenProduccionInputDTO>() {

      @Override
      public OrdenProduccionInputDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        ordenProduccion.setuDestino2(rs.getString("LGPLA"));
        resultDT.setId(1);
        resultDT.setMsg("DEPA encontrada");

        ordenProduccion.setResultDT(resultDT);

        return ordenProduccion;
      }
    }, args);
  }

  @Override
  public OrdenProduccionInputDTO obtieneReservaUbicacionHU1(OrdenProduccionInputDTO ordenProduccionInput) {
    String query = "SELECT * FROM ZPickingEntregaEntrante WITH(NOLOCK) "
        + "WHERE usuarioMontacarga = ? AND VBELN = ? AND status = 1 AND idProceso = '2'";
    Object[] args = { ordenProduccionInput.getUsuarioMontacarga(), ordenProduccionInput.getOrdeProduccion() };
    OrdenProduccionInputDTO ordenProduccion = new OrdenProduccionInputDTO();
    ResultDTO resultDT = new ResultDTO();

    return jdbcTemplate.queryForObject(query, new RowMapper<OrdenProduccionInputDTO>() {

      @Override
      public OrdenProduccionInputDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        ordenProduccion.setuOrigen0(rs.getString("LGNUM"));
        ordenProduccion.setuOrigen1(rs.getString("LGTYP"));
        ordenProduccion.setuOrigen2(rs.getString("LGPLA"));

        resultDT.setId(1);
        resultDT.setMsg("Reserva de carril");
        ordenProduccion.setResultDT(resultDT);

        return ordenProduccion;
      }
    }, args);
  }

  @Override
  public int reservaUbicacionHU1(OrdenProduccionInputDTO ordenProduccionInput) {
    String query = "UPDATE TOP (1) ZPickingEntregaEntrante SET usuarioMontacarga = ? , Status = '1' WHERE "
        + "usuarioMontacarga IS null AND EXIDV IS null AND Status IS null AND VBELN = ? AND idProceso = '2'";
    Object[] args = { ordenProduccionInput.getUsuarioMontacarga(), ordenProduccionInput.getOrdeProduccion() };

    return jdbcTemplate.update(query, args);
  }

  @Override
  public int reservaUbicacionHU2(OrdenProduccionInputDTO ordenProduccionInput) {
    String query = "UPDATE TOP (1) ZPickingEntregaEntrante SET usuarioMontacarga = ?, Status='1' "
        + "WHERE usuarioMontacarga IS null AND EXIDV IS null AND Status IS null AND VBELN = ? AND idProceso='2' AND LGNUM+LGTYP+LGPLA IN "
        + "(SELECT LGNUM+LGTYP+LGPLA FROM ZPickingEntregaEntrante WITH(NOLOCK) "
        + "WHERE Status = '1' AND usuarioMontacarga = ?)";
    Object[] args = { ordenProduccionInput.getUsuarioMontacarga(), ordenProduccionInput.getOrdeProduccion(),
        ordenProduccionInput.getUsuarioMontacarga() };

    return jdbcTemplate.update(query, args);
  }

  @Override
  public ResultDTO validaPickeoPrevioHU(OrdenProduccionInputDTO ordenProduccionInput, String hu) {
    String query = "SELECT EXIDV FROM ZPickingEntregaEntrante WITH(NOLOCK) WHERE EXIDV = ? AND VBELN = ? AND idProceso = '2'";
    Object[] args = { hu, ordenProduccionInput.getOrdeProduccion() };

    return jdbcTemplate.queryForObject(query, new RowMapper<ResultDTO>() {

      @Override
      public ResultDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultDTO result = new ResultDTO();
        if (rs.next()) {
          result.setId(2);
          result.setMsg("El HU ya fue consumido");

        } else {
          result.setId(1);
          result.setMsg("HU sin confirmar");
        }
        return result;
      }
    }, args);
  }

  @Override
  public ResultDTO validarEntregaPickin(OrdenProduccionInputDTO ordenProduccionInput) {
    String query = "SELECT DISTINCT (VBELN), MATNR FROM ZPickingEntregaEntrante WITH(NOLOCK) "
        + "WHERE VBELN = ? AND WERKS = ? AND idProceso = '2'";
    Object[] args = { ordenProduccionInput.getOrdeProduccion(), ordenProduccionInput.getWerks() };

    return jdbcTemplate.queryForObject(query, new RowMapper<ResultDTO>() {

      @Override
      public ResultDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultDTO result = new ResultDTO();
        if (rs.next()) {
          result.setId(1);
          result.setTypeS(rs.getString("MATNR"));
          result.setMsg("ORDEN DE PRODUCCION YA SE ENCUENTRA EN PICKING");
        } else {
          result.setId(2);
          result.setMsg("ORDEN DE PRODUCCION NO DISPONIBLE PARA PICKING");
        }

        return result;
      }
    }, args);
  }
}
