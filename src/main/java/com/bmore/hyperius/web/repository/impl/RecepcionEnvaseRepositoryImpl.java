package com.bmore.hyperius.web.repository.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.web.dto.CarrilUbicacionDTO;
import com.bmore.hyperius.web.dto.CarrilesUbicacionDTO;
import com.bmore.hyperius.web.dto.EntregaDTO;
import com.bmore.hyperius.web.dto.EntregaDetalleDTO;
import com.bmore.hyperius.web.dto.EntregaDetalleDTOItem;
import com.bmore.hyperius.web.dto.EntregaInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.RecepcionEnvaseRepository;
import com.bmore.hyperius.web.utils.Utils;

@Repository
public class RecepcionEnvaseRepositoryImpl implements RecepcionEnvaseRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  public EntregaDTO getEntrega(EntregaDTO entregaInput) {
    EntregaDTO entrega = new EntregaDTO();
    ResultDTO result = new ResultDTO();

    try {
      String query_1 = String.format(
          "select DISTINCT(LIKP.VBELN),LFART from HCMDB.dbo.LIKP LIKP with(nolock) where LIKP.VBELN = '%s';",
          entregaInput.getEntrega());
      Map<String, Object> row_1 = jdbcTemplate.queryForMap(query_1);

      String query_2;
      String lfart = (String) row_1.get("LFART");
      if (lfart.equals("EL") || lfart.equals("YD15") || lfart.equals("YD06")) {
        entregaInput.setLfart(lfart);
        entrega.setLfart(lfart);
        query_2 = String.format(
            "select DISTINCT(LIKP.VBELN) from HCMDB.dbo.LIKP LIKP with(nolock) inner join HCMDB.dbo.LIPS LIPS with(nolock) "
                +
                "on LIKP.VBELN = LIPS.VBELN where (LIKP.LFART = 'EL' or LIKP.LFART = 'YD15' or LIKP.LFART = 'YD06' ) and LIKP.VBELN= '%s' "
                +
                "and (LIPS.WERKS= '%s' or LIKP.KUNNR=(select top(1) kunnr from zCentrosBCPS where werks= '%s'));",
            entregaInput.getEntrega(), entregaInput.getWerks(), entregaInput.getWerks());
      } else {
        entregaInput.setLfart("Y015");
        entrega.setLfart("Y015");
        query_2 = String.format(
            "select DISTINCT(LIKP.VBELN),LIPS.WERKS from HCMDB.dbo.LIKP LIKP with(nolock) inner join HCMDB.dbo.LIPS LIPS "
                +
                "with(nolock) on LIKP.VBELN = LIPS.VBELN inner join zContingencia zContingencia with(nolock) on LIKP.VBELN = zContingencia.ENTREGA "
                +
                "where LIKP.LFART = 'Y015' and LIKP.VBELN = '%s' and LIKP.KUNNR = (select kunnr from zCentrosBCPS where werks= '%s');",
            entregaInput.getEntrega(), entregaInput.getWerks());
      }

      try {
        String query_3;
        Map<String, Object> row_2 = jdbcTemplate.queryForMap(query_2);

        if (lfart.equals("EL") || lfart.equals("YD15")) {
          query_3 = "select VBELN,dbo.conFec(ERDAT) as ERDAT,LIKP.LIFNR,LFA1.NAME1,LFA1.NAME2, LFA1.ORT01, LFA1.ORT02,LFA1.PSTLZ,LFA1.STRAS from "
              +
              "HCMDB.dbo.LIKP LIKP with(nolock) left outer join  HCMDB.dbo.LFA1 LFA1 on (LIKP.LIFNR= LFA1.LIFNR or LIKP.VSTEL = LFA1.LIFNR ) "
              +
              "where VBELN= '%s';";
        } else if (lfart.equals("Y015")) {
          entrega.setWerksBCPS((String) row_2.get("WERKS"));
          query_3 = "select VBELN,dbo.conFec(ERDAT) as ERDAT,LIKP.LIFNR,LFA1.NAME1,LFA1.NAME2, LFA1.ORT01, LFA1.ORT02,LFA1.PSTLZ,LFA1.STRAS from "
              +
              "HCMDB.dbo.LIKP LIKP with(nolock) left outer join  HCMDB.dbo.LFA1 LFA1 on (select lifnr from zCentrosBCPS where vstel= LIKP.VSTEL) "
              +
              "= LFA1.LIFNR  where VBELN= '%s';";
        } else {
          query_3 = "SELECT VBELN, dbo.conFec(ERDAT) AS ERDAT, LP.KUNNR AS LIFNR, KN.NAME1, KN.NAME2, KN.ORT01, '' AS ORT02, KN.PSTLZ, KN.STRAS  "
              +
              "FROM LIKP LP WITH(NOLOCK) LEFT OUTER JOIN KNA1 KN WITH(NOLOCK) ON LP.KUNNR = KN.KUNNR WHERE VBELN = '%s';";
        }

        try {
          query_3 = String.format(query_3, entregaInput.getEntrega());
          Map<String, Object> row_3 = jdbcTemplate.queryForMap(query_3);

          entrega.setEntrega((String) row_3.get("VBELN"));
          entrega.setProveedor((String) row_3.get("LIFNR"));
          entrega.setFechaDocumento((String) row_3.get("ERDAT"));

          String name1 = (String) row_3.get("NAME1");
          String name2 = (String) row_3.get("NAME2");
          String ort01 = (String) row_3.get("ORT01");
          String ort02 = (String) row_3.get("ORT02");
          String pstlz = (String) row_3.get("PSTLZ");
          String stras = (String) row_3.get("STRAS");

          entrega.setProveedorDesc(name1 + " " + name2 + ", " + ort01 + ", " + ort02 + ", " + pstlz + ", " + stras);
          result.setId(1);
          result.setMsg("Recuperacion de Datos de Cabecera Correcta");
        } catch (Exception e) {
          e.printStackTrace();
          result.setId(5);
          result.setMsg("La entrega no tiene datos de cabecera para mostrar");
        }

      } catch (Exception e) {
        e.printStackTrace();
        result.setId(2);
        result.setMsg("ENTREGA NO EXISTE");
      }

    } catch (Exception e) {
      e.printStackTrace();
      result.setId(2);
      result.setMsg("ENTREGA NO EXISTE");
    }

    entrega.setResultDT(result);
    return entrega;
  }

  @Override
  public HashMap<String, String> getLgortsEntrega(String entrega) {
    HashMap<String, String> map = new HashMap<>();
    map.put("resultDT.id", "3");
    map.put("resultDT.msg", "No fue posible recuperar el  campo LGORT de la entrega (LIPS)");

    String sql = String
        .format("select DISTINCT(LIPS.LGORT) from HCMDB.dbo.LIPS LIPS with(nolock) where LIPS.VBELN = '%s';", entrega);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

    for (Map<String, Object> row : rows) {
      map.put((String) row.get("LGORT"), (String) row.get("LGORT"));
      map.put("resultDT.id", "1");
      map.put("resultDT.msg", "LGORT de la entrega (LIPS) recuperado con exitosamente");
    }

    return map;
  }

  @Override
  public HashMap<String, String> getLgortsTabla() {
    HashMap<String, String> map = new HashMap<String, String>();

    map.put("resultDT.id", "3");
    map.put("resultDT.msg", "No fue posible recuperar los valores LGORT de la tabla (TB_BCPS_LGORT_VBELN_IM)");

    String sql = "SELECT LGORT FROM TB_BCPS_LGORT_VBELN_IM WHERE ID_PROCESO = 1;";
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

    for (Map<String, Object> row : rows) {
      map.put((String) row.get("LGORT"), (String) row.get("LGORT"));
      map.put("resultDT.id", "1");
      map.put("resultDT.msg", "LGORTs de las tabla (TB_BCPS_LGORT_VBELN_IM)");
    }

    return map;
  }

  @Override
  public EntregaDTO getEntregaDetalle(EntregaDTO entrega) {

    ResultDTO result = new ResultDTO();
    EntregaDetalleDTOItem entregaDetalleDTOItem = new EntregaDetalleDTOItem();
    List<EntregaDetalleDTO> items = new ArrayList<>();

    String sql_1 = String.format("select count(POSNR) from HCMDB.dbo.LIPS with(nolock) where VBELN = '%s';",
        entrega.getEntrega());
    Integer cantidad_1 = jdbcTemplate.queryForObject(sql_1, Integer.class);
    assert cantidad_1 != null;
    if (cantidad_1 > 0) {
      String sql_2 = String
          .format("select POSNR, MATNR, ARKTX from HCMDB.dbo.LIPS with(nolock) where VBELN= '%s' and PSTYV in " +
              "(select tipo_posicion FROM TB_BCPS_TIPO_DOC_POS where ID_PROC = 1) and convert(decimal, LFIMG) > 0 and LGORT!='TA01' "
              +
              "and LGORT!='TA02';", entrega.getEntrega());
      List<Map<String, Object>> rows_2 = jdbcTemplate.queryForList(sql_2);
      HashMap<String, String> map = new HashMap<>();

      for (Map<String, Object> row_2 : rows_2) {
        String matnr = (String) row_2.get("MATNR");
        if (map.get(matnr) == null) {
          map.put(matnr, matnr);
          EntregaDetalleDTO item = new EntregaDetalleDTO();
          item.setMaterial((String) row_2.get("MATNR"));
          item.setPosicion((String) row_2.get("POSNR"));
          item.setDescripcion((String) row_2.get("ARKTX"));

          String sql_3;
          if (entrega.getLfart().equals("EL") || entrega.getLfart().equals("YD15")
              || entrega.getLfart().equals("YD06")) {
            sql_3 = "select count(VENUM) from HCMDB.dbo.VEPO WITH(NOLOCK) where VBELN = '%s' and MATNR = '%s';";
          } else {
            if (entrega.getWerksBCPS() != null && (entrega.getWerksBCPS().equals("EMZ1")) ||
                (entrega.getWerksBCPS().equals("TMZ1")) || (entrega.getWerksBCPS().toUpperCase().startsWith("PC"))) {
              sql_3 = "select count(LENUM) from LQUA WITH(NOLOCK) where LENUM in (select hu from zContingencia WITH(NOLOCK) "
                  +
                  "where ENTREGA = '%s' and IDPROC=8 AND HU IS NOT NULL ) and MATNR = '%s' and SKZUA is not null;";
            } else {
              sql_3 = "select count(venum) from vepo WITH(NOLOCK) where VENUM in (select venum from VEKP WITH(NOLOCK) "
                  +
                  "where EXIDV in (select hu from zContingencia WITH(NOLOCK) where ENTREGA = '%s' and " +
                  "IDPROC=28 AND HU IS NOT NULL )) and VELIN ='1' and MATNR = '%s';";
            }
          }

          sql_3 = String.format(sql_3, entrega.getEntrega(), item.getMaterial());
          Integer cantidad_2 = jdbcTemplate.queryForObject(sql_3, Integer.class);
          assert cantidad_2 != null;
          if (cantidad_2 > 0) {
            item.setHus(cantidad_2.toString());
            item.setEmbalar("false");
          } else {
            item.setHus("NO EXISTE");
            item.setEmbalar("true");
          }

          String sql_4;
          if (entrega.getLfart().equals("EL")) {
            sql_4 = "select sum(convert(decimal(18, 3), LFIMG)) as val1, VRKME as val2 from HCMDB.dbo.LIPS " +
                "WITH(NOLOCK) where VBELN = '%s' and MATNR = '%s' and PSTYV in " +
                "(select tipo_posicion FROM TB_BCPS_TIPO_DOC_POS where ID_PROC = 1) and " +
                "convert(decimal, LFIMG) > 0 group by VRKME";
          } else {
            sql_4 = "select sum(convert(decimal(18, 3), LFIMG)) as val1, MEINS as val2 from HCMDB.dbo.LIPS " +
                "WITH(NOLOCK) where VBELN = '%s' and MATNR = '%s' and PSTYV in " +
                "(select tipo_posicion FROM TB_BCPS_TIPO_DOC_POS where ID_PROC = 1) and " +
                "convert(decimal, LFIMG) > 0 group by MEINS";
          }

          try {
            sql_4 = String.format(sql_4, entrega.getEntrega(), item.getMaterial());
            Map<String, Object> row_4 = jdbcTemplate.queryForMap(sql_4);

            double val1 = (double) row_4.get("val1");
            item.setCajas(String.valueOf(new BigDecimal(val1).setScale(3, RoundingMode.HALF_UP)));
            item.setMe((String) row_4.get("val2"));
          } catch (Exception e) {
            e.printStackTrace();
            item.setCajas("NO EXISTE");
          }

          item.setHusPendientes("0");
          // Agregar LGNUM N/A = aun no se ha elegido un LGNUM
          // para esta entrega, unicamente es uno por entrega
          // indepentientemente de las posiciones que tenga
          item.setLgnum("N/A");

          try {
            String sql_5 = String.format("SELECT count(MATNR) as cantidad, LGNUM FROM " +
                "HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where vbeln = '%s' and " +
                "matnr = '%s' and status is not null and EXIDV is not null and idProceso='1' " +
                "group by LGNUM;", entrega.getEntrega(), item.getMaterial());
            Map<String, Object> row_5 = jdbcTemplate.queryForMap(sql_5);
            item.setHusPendientes((String) row_5.get("cantidad"));
            item.setLgnum((String) row_5.get("LGNUM"));
          } catch (Exception e) {
            e.printStackTrace();
          }

          log.info("Buscando HUs asignadas");
          item.setHusAsignadas("0");
          // tambien se toma en cuenta el lgnum de las hus en
          // proceso de picking, "sustituye" (siempre es el
          // mismo)
          // al que ya fue pickeado, en caso de que ninguna hu
          // este confirmada se arrastro de las que ya
          // fueron
          // confirmadas o bien N/A si no hay confirmadas

          try {
            String sql_6 = String.format("SELECT count(matnr) as cantidad,LGNUM FROM " +
                "HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where vbeln = '%s' and matnr = '%s' +" +
                "and idProceso = '1' group by LGNUM", entrega.getEntrega(), item.getMaterial());
            Map<String, Object> row_6 = jdbcTemplate.queryForMap(sql_6);

            item.setHusAsignadas((String) row_6.get("cantidad"));
            item.setLgnum((String) row_6.get("LGNUM"));
          } catch (Exception e) {
            e.printStackTrace();
          }

          items.add(item);
        }
      }

      result.setId(1);
      result.setMsg("Detalle de entrega encontrado.");

    } else {
      result.setId(2);
      result.setMsg("Detalle de entrega NO encontrado.");
    }

    entregaDetalleDTOItem.setItem(items);
    entrega.setItems(entregaDetalleDTOItem);
    entrega.setResultDT(result);

    return entrega;
  }

  @Override
  public EntregaDTO getEntregaDetalleSoloCabecera(String vbeln) {
    ResultDTO result = new ResultDTO();
    EntregaDTO entregaDTO = new EntregaDTO();
    EntregaDetalleDTOItem entregaDetalleDTOItem = new EntregaDetalleDTOItem();
    List<EntregaDetalleDTO> items = new ArrayList<>();
    HashMap<String, String> map = new HashMap<>();

    String sql = String.format("select POSNR, MATNR, ARKTX from HCMDB.dbo.LIPS with(nolock) where VBELN = '%s' and " +
        "PSTYV in (select tipo_posicion FROM TB_BCPS_TIPO_DOC_POS where ID_PROC = 1) and " +
        "convert(decimal, LFIMG) > 0 and LGORT != 'TA01' and LGORT != 'TA02';", vbeln);

    result.setId(2);
    result.setMsg("Detalle de entrega NO encontrado.");

    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    for (Map<String, Object> row : rows) {
      result.setId(1);
      result.setMsg("Detalle de entrega encontrado.");

      String matnr = (String) row.get("MATNR");
      if (map.get(matnr) == null) {
        map.put(matnr, matnr);
        EntregaDetalleDTO item = new EntregaDetalleDTO();
        item.setMaterial((String) row.get("MATNR"));
        item.setPosicion((String) row.get("POSNR"));
        item.setDescripcion((String) row.get("ARKTX"));
        items.add(item);
      }
    }

    entregaDetalleDTOItem.setItem(items);
    entregaDTO.setItems(entregaDetalleDTOItem);
    entregaDTO.setResultDT(result);
    return entregaDTO;
  }

  @Override
  public HashMap<String, String> getCarrilesBloqueados(String idProceso, String werks) {
    HashMap<String, String> map = new HashMap<>();
    String sql = String.format("select distinct(CARRIL) FROM ZPickingEntregaEntrante WITH(NOLOCK) WHERE " +
        "idProceso!= '%s' AND werks = '%s' and (Status  is null or Status ='1');", idProceso, werks);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

    for (Map<String, Object> row : rows) {
      map.put((String) row.get("CARRIL"), "");
    }
    return map;
  }

  @Override
  public CarrilesUbicacionDTO getCarriles(String WERKS, String MATNR, String ID_PR, String ID_PR_Z, String LGORT,
      String VBELN, HashMap<String, String> carrilesBloqueados) {
    CarrilesUbicacionDTO carrilesDTO = new CarrilesUbicacionDTO();
    List<CarrilUbicacionDTO> carrilList = new ArrayList<>();
    HashMap<String, String> hashMap = new HashMap<>();

    String sql_1 = String.format(
        "EXEC SP_BCPS_GET_CARRILES_POR_MATERIAL '%s', '%s', '%s', '%s', '%s';",
        MATNR,
        WERKS,
        ID_PR,
        ID_PR_Z,
        1);

    List<Map<String, Object>> rows_1 = jdbcTemplate.queryForList(sql_1);
    for (Map<String, Object> row_1 : rows_1) {
      String lgnum = (String) row_1.get("LGNUM");
      String lgtyp = (String) row_1.get("LGTYP");
      String lgpla = (String) row_1.get("LGPLA");

      if (carrilesBloqueados.get(lgnum.concat(lgtyp).concat(lgpla).trim()) == null) {
        CarrilUbicacionDTO item = new CarrilUbicacionDTO();
        item.setLgnum(lgnum);
        item.setLgtyp(lgtyp);
        item.setLgpla(lgpla);
        item.setCantidadHus((String) row_1.get("TOTAL"));
        item.setCajas((String) row_1.get("VERME"));
        item.setMe((String) row_1.get("MEINS"));
        item.setMaxle((String) row_1.get("MAXLE"));

        hashMap.put(item.getLgnum() + item.getLgtyp() + item.getLgpla(), "X");
        carrilList.add(item);
      }
    }

    String sql_2 = String.format(
        "EXEC SP_GET_CARRILES_VACIOS '%s', '%s', '%s', '%s', '%s';",
        MATNR,
        LGORT,
        WERKS,
        ID_PR,
        ID_PR_Z);

    List<Map<String, Object>> rows_2 = jdbcTemplate.queryForList(sql_2);
    for (Map<String, Object> row_2 : rows_2) {
      String lgnum = (String) row_2.get("LGNUM");
      String lgtyp = (String) row_2.get("LGTYP");
      String lgpla = (String) row_2.get("LGPLA");

      if (carrilesBloqueados.get(lgnum.concat(lgtyp).concat(lgpla).trim()) == null) {
        CarrilUbicacionDTO item = new CarrilUbicacionDTO();
        item.setLgnum(lgnum);
        item.setLgtyp(lgtyp);
        item.setLgpla(lgpla);
        item.setCantidadHus("0");
        hashMap.put(item.getLgnum() + item.getLgtyp() + item.getLgpla(), "X");
        carrilList.add(item);
      }
    }

    String sql_3 = String.format("SELECT distinct LGNUM, LGTYP, LGPLA FROM ZPickingEntregaEntrante " +
        "with(nolock) where VBELN = '%s' and werks = '%s' and idProceso = '%s' and " +
        "matnr = '%s' and (Status  is null or Status ='1');", VBELN, WERKS, ID_PR_Z, MATNR);
    List<Map<String, Object>> rows_3 = jdbcTemplate.queryForList(sql_3);

    for (Map<String, Object> row_3 : rows_3) {
      String lgnum = (String) row_3.get("LGNUM");
      String lgtyp = (String) row_3.get("LGTYP");
      String lgpla = (String) row_3.get("LGPLA");

      if (carrilesBloqueados.get(lgnum + lgtyp + lgpla.trim()) != null) {
        CarrilUbicacionDTO item = new CarrilUbicacionDTO();
        item.setLgnum(lgnum);
        item.setLgtyp(lgtyp);
        item.setLgpla(lgpla);
        item.setCantidadHus("-1");
        if (hashMap.get(item.getLgnum() + item.getLgtyp() + item.getLgpla()) == null) {
          carrilList.add(0, item);
        }
      }

    }

    for (CarrilUbicacionDTO item : carrilList) {
      if (Integer.parseInt(item.getCantidadHus().trim()) != 0) {
        if (Integer.parseInt(item.getCantidadHus().trim()) == -1) {
          item.setCantidadHus("0");
        }

        String sql_4 = String.format(
            "select count(*) from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where LGNUM = '%s' " +
                "and LGTYP = '%s' and LGPLA = '%s' and idProceso='1' and Status is null;",
            item.getLgnum(), item.getLgtyp(), item.getLgpla());
        Integer cantidad = jdbcTemplate.queryForObject(sql_4, Integer.class);
        assert cantidad != null;
        item.setHusPendientes(cantidad.toString());
      }
    }

    carrilesDTO.setItem(carrilList);
    return carrilesDTO;
  }

  @Override
  public ResultDTO ingresaDetalleEnvase(String VBELN, CarrilesUbicacionDTO carrilesDTO, String user, String werks) {
    ResultDTO resultDT = new ResultDTO();

    for (int x = 0; x < carrilesDTO.getItem().size(); x++) {

      BigDecimal bigDecimal = new BigDecimal(carrilesDTO.getItem().get(x).getAsignarHus().trim());

      int ingresarZpicking = Integer.parseInt(bigDecimal.toBigInteger() + "");

      for (int y = 0; y < ingresarZpicking; y++) {

        String sql = "INSERT INTO HCMDB.dbo.ZPickingEntregaEntrante (VBELN, LGNUM, LGTYP, LGPLA, MATNR, marcaTiempo, usuarioSupervisor, idProceso, werks, carril) values(?, ?, ?, ?, ?, getdate(), ?, ?, ?, ?);";
        Object[] args = {
            VBELN,
            carrilesDTO.getItem().get(x).getLgnum(),
            carrilesDTO.getItem().get(x).getLgtyp(),
            carrilesDTO.getItem().get(x).getLgpla(),
            Utils.zeroFill(carrilesDTO.getItem().get(x).getMaterial(), 18),
            user,
            "1",
            werks,
            carrilesDTO.getItem().get(x).getLgnum() + carrilesDTO.getItem().get(x).getLgtyp()
                + carrilesDTO.getItem().get(x).getLgpla()
        };
        jdbcTemplate.update(sql, args);
      }
    }
    resultDT.setId(1);
    resultDT.setMsg("Se registro la entrega entrante correctamete, mandar a montacarga");

    return resultDT;
  }

  @Override
  public EntregaInputDTO validarEntregaPickin(String entry) {
    EntregaInputDTO entrega = new EntregaInputDTO();
    ResultDTO result = new ResultDTO();
    HashMap<String, String> map = new HashMap<>();

    String sql = "select distinct(MATNR) from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN = '%s';";
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

    for (Map<String, Object> row : rows) {
      String matnr = (String) row.get("MATNR");
      map.put(matnr, "MATNR");
    }

    if (rows.isEmpty()) {
      result.setId(2);
      result.setMsg("ENTREGA NO DISPONIBLE PARA PICKING");
    } else {
      result.setId(1);
      result.setMsg("ENTREGA YA SE ENCUENTRA EN PICKING");
    }

    entrega.setMateriales(map);
    entrega.setResultDT(result);
    return entrega;
  }

  @Override
  public ResultDTO validarHU_(String entry, String HU) {
    ResultDTO result = new ResultDTO();
    String sql = String
        .format("SELECT count(VEKP.EXIDV) from HCMDB.dbo.VEKP VEKP WITH(NOLOCK) INNER JOIN HCMDB.dbo.VEPO " +
            "VEPO WITH(NOLOCK) on VEKP.VENUM = VEPO.VENUM where VEKP.VPOBJKEY= '%s' and VEKP.EXIDV= '%s';", entry, HU);
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
    if (count == null) {
      result.setId(2);
      result.setMsg("HU NO EXISTENTE");
    } else {
      if (count > 0) {
        result.setId(1);
        result.setMsg("HU EXISTENTE");
      } else {
        result.setId(2);
        result.setMsg("HU NO EXISTENTE");
      }
    }

    return result;
  }

  @Override
  public ResultDTO VRPTVALUE(String entry, String HU) {
    ResultDTO result = new ResultDTO();

    String sql = String.format("SELECT count(EXIDV) from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) " +
        "where EXIDV= '%s' and VBELN = '%s';", HU, entry);
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
    if (count == null) {
      result.setId(3);
      result.setMsg("HU NO ENCONTRADA");
    } else {
      if (count > 0) {
        result.setId(2);
        result.setMsg("HU YA FUE INGRESADA");
      } else {
        result.setId(1);
        result.setMsg("HU LIBRE");
      }
    }

    return result;
  }

  @Override
  public ResultDTO borrarZHU(String entry, String HU) {
    return null;
  }

  @Override
  public EntregaInputDTO getPositions(String entry, String HU) {
    ResultDTO result = new ResultDTO();
    EntregaInputDTO entrega = new EntregaInputDTO();

    try {
      String sql = String.format("select LGNUM, LGTYP, LGPLA from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) " +
          "where VBELN = '%s' and EXIDV = '%s';", entry, HU);
      Map<String, Object> row = jdbcTemplate.queryForMap(sql);

      result.setId(1);
      entrega.setuDestino1((String) row.get("LGTYP"));
      entrega.setuDestino2((String) row.get("LGPLA"));
    } catch (Exception e) {
      e.printStackTrace();
      result.setId(2);
      result.setMsg("Error De Comunicacion");
    }

    entrega.setResultDT(result);
    return entrega;
  }

  @Override
  public ResultDTO entryContabilizada(String entry) {
    return null;
  }

  @Override
  public int getFaltantes(String entry) {
    String sql = String.format("select count(*) from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) " +
        "where VBELN = '%s' and Status is null;", entry);
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
    int x;

    if (count == null) {
      x = 999999;
    } else {
      if (count >= 0) {
        x = count;
      } else {
        x = 0;
      }
    }

    return x;
  }

  @Override
  public ResultDTO contabilizadoOK(String entry) {
    ResultDTO resultDT = new ResultDTO();

    try {
      String sql = String.format("select * from HCMDB.dbo.zContingencia WITH(NOLOCK) where (IDPROC = '4' " +
          "or IDPROC='14' or IDPROC='32' ) and ENTREGA = '%s';", entry);
      Map<String, Object> row = jdbcTemplate.queryForMap(sql);
      resultDT.setId(1);
      resultDT.setMsg("Entrega contabilizada");
    } catch (Exception e) {
      e.printStackTrace();
      resultDT.setId(0);
      resultDT.setMsg("Entrega aun no contabilizada");
    }

    return resultDT;
  }

  @Override
  public ResultDTO getVBELNFromHuSAP(String hu, String werks) {
    ResultDTO resultDT = new ResultDTO();
    resultDT.setTypeS("");

    try {
      String sql = String.format("SELECT VEPO.VBELN, LIKP.LFART from HCMDB.dbo.VEPO VEPO WITH(NOLOCK) inner join " +
          "HCMDB.dbo.VEKP VEKP WITH(NOLOCK) on VEPO.VENUM = VEKP.VENUM and VEKP.EXIDV= '%s' and VEPO.VELIN = '1' inner join "
          +
          "LIKP LIKP on VEPO.VBELN= LIKP.VBELN", hu);

      Map<String, Object> row = jdbcTemplate.queryForMap(sql);
      resultDT.setTypeS((String) row.get("VBELN"));
      resultDT.setMsg((String) row.get("LFART"));
      resultDT.setId(1);
    } catch (Exception e) {
      e.printStackTrace();
      resultDT.setId(0);
      resultDT.setMsg("Entrega no encontrada vía HU y con centro de montacarguista: " + werks);
    }

    return resultDT;
  }

  @Override
  public ResultDTO getVBELNFromHuBCPS(String hu, String werks) {
    ResultDTO resultDT = new ResultDTO();
    resultDT.setTypeS("");

    try {
      String sql = String.format("select ENTREGA as VBELN from zContingencia zCon WITH(NOLOCK) " +
          "inner join HCMDB.dbo.LIKP LIKP WITH(NOLOCK) on zCon.ENTREGA = LIKP.VBELN and " +
          "LIKP.KUNNR = (select KUNNR from zCentrosBCPS WITH(NOLOCK) where werks = '%s') and " +
          "zCon.HU= '%s' and (zCon.IDPROC=28 or zCon.IDPROC = 8);", werks, hu);
      Map<String, Object> row = jdbcTemplate.queryForMap(sql);

      resultDT.setTypeS((String) row.get("VBELN"));
      resultDT.setId(1);
      resultDT.setMsg("Entrega encontrada");
    } catch (Exception e) {
      e.printStackTrace();
      resultDT.setId(0);
      resultDT.setMsg("Entrega no encontrada vía HU y con centro de montacarguista: " + werks);
    }

    return resultDT;
  }

  @Override
  public ResultDTO validarEntregaEnPicking(String VBELN) {
    ResultDTO resultDT = new ResultDTO();

    try {
      String sql = String.format("SELECT VBELN from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBLEN= '%s';",
          VBELN);
      Map<String, Object> row = jdbcTemplate.queryForMap(sql);
      resultDT.setId(1);
      resultDT.setMsg("Entrega en Picking");
    } catch (Exception e) {
      e.printStackTrace();
      resultDT.setId(0);
      resultDT.setMsg("La entrega no está en picking");
    }

    return resultDT;
  }

  @Override
  public ResultDTO reservarCarrilHU(String entrega, String hu, String matnr, String usuarioMontacargas) {
    ResultDTO result = new ResultDTO();

    try {
      String sql = String.format("UPDATE TOP(1) HCMDB.dbo.ZPickingEntregaEntrante set EXIDV = '%s', " +
          "usuarioMontacarga = '%s' where VBELN = '%s' and MATNR = '%s'  and  Status is null and " +
          "EXIDV is null and idProceso = 1", hu, usuarioMontacargas, entrega, matnr);
      Map<String, Object> row = jdbcTemplate.queryForMap(sql);

      result.setId(1);
      result.setMsg("LUGAR RESERVADO PARA HU");
    } catch (Exception e) {
      e.printStackTrace();
      result.setId(0);
      result.setMsg("NO FUE POSIBLE RESERVAR HU");
    }

    return result;
  }

  @Override
  public CarrilUbicacionDTO consultReservaCarrilHu(String vbeln, String hu) {
    CarrilUbicacionDTO carrilUbicacionDTO = new CarrilUbicacionDTO();
    ResultDTO resultDT = new ResultDTO();

    try {
      String sql = String
          .format("SELECT LGNUM, LGTYP, LGPLA, STATUS from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) " +
              "where VBELN = '%s' and EXIDV = '%s' and idProceso = 1;", vbeln, hu);
      Map<String, Object> row = jdbcTemplate.queryForMap(sql);

      carrilUbicacionDTO.setLgnum((String) row.get("LGNUM"));
      carrilUbicacionDTO.setLgtyp((String) row.get("LGTYP"));
      carrilUbicacionDTO.setLgpla((String) row.get("LGPLA"));

      resultDT.setId(1);
      resultDT.setMsg("HU1 encontrada");
      resultDT.setTypeS((String) row.get("STATUS"));
    } catch (Exception e) {
      e.printStackTrace();
      resultDT.setId(0);
      resultDT.setMsg("El HU no esta diponible para pickear");
    }

    carrilUbicacionDTO.setResultDT(resultDT);
    return carrilUbicacionDTO;
  }

  @Override
  public EntregaDetalleDTO getDataHU(String hu) {
    ResultDTO result = new ResultDTO();
    EntregaDetalleDTO entrega = new EntregaDetalleDTO();

    try {
      String sql = String
          .format("select vepo.matnr as matnr, maktx as maktx, vepo.vemng as vemng, vepo.VEMEH as VEMEH from "
              + "HCMDB.dbo.MAKT MAKT WITH(NOLOCK) inner join HCMDB.dbo.VEPO VEPO WITH(NOLOCK) "
              + "on VEPO.matnr=MAKT.matnr inner join HCMDB.dbo.VEKP VEKP WITH(NOLOCK) on "
              + "VEKP.venum = VEPO.venum where VEKP.EXIDV = '%s' and VEPO.VELIN ='1'", hu);
      Map<String, Object> row = jdbcTemplate.queryForMap(sql);

      result.setId(1);
      result.setMsg("Material encontrado");

      entrega.setMaterial((String) row.get("matnr"));
      entrega.setDescripcion((String) row.get("maktx"));
      entrega.setCajas((String) row.get("vemng"));
      entrega.setMe((String) row.get("VEMEH"));
    } catch (Exception e) {
      e.printStackTrace();
      result.setId(2);
      result.setMsg("Material no encontrado en la entrega entrante.");
    }

    entrega.setResultDT(result);
    return entrega;
  }

  @Override
  public EntregaDetalleDTO getDataHU_LQUA(String hu) {
    ResultDTO result = new ResultDTO();
    EntregaDetalleDTO entrega = new EntregaDetalleDTO();

    try {
      String sql = String
          .format("SELECT LQUA.MATNR AS MATNR, MAKT.MAKTX AS MAKTX, LQUA.VERME AS VEMNG, MEINS AS VEMEH FROM " +
              "LQUA LQUA INNER JOIN MAKT MAKT WITH(NOLOCK) ON LQUA.MATNR = MAKT.MATNR WHERE LENUM = '%s';", hu);
      Map<String, Object> row = jdbcTemplate.queryForMap(sql);
      result.setId(1);
      result.setMsg("Material encontrado");

      entrega.setMaterial((String) row.get("matnr"));
      entrega.setDescripcion((String) row.get("maktx"));
      entrega.setCajas((String) row.get("vemng"));
      entrega.setMe((String) row.get("VEMEH"));
    } catch (Exception e) {
      e.printStackTrace();
      result.setId(2);
      result.setMsg("Material no encontrado en la entrega entrante.");
    }

    entrega.setResultDT(result);
    return entrega;
  }

  @Override
  public ResultDTO confirmaPickingHU(String VBELN, String hu) {
    ResultDTO result = new ResultDTO();
    String sql = "update HCMDB.dbo.ZPickingEntregaEntrante set Status = 'X' where VBELN = ? and EXIDV = ?;";
    Object[] args = { VBELN, hu };

    result.setId(0);
    result.setMsg("NO FUE POSIBLE CONFIRMAR HU EN UBICACION");

    jdbcTemplate.update(sql, args);
    result.setId(1);
    result.setMsg("HU CONFIRMADA EN UBICACION");

    return result;
  }

  @Override
  public CarrilesUbicacionDTO compararUbicacionesHUs(String hu1, String hu2) {
    CarrilesUbicacionDTO carriles = new CarrilesUbicacionDTO();
    List<CarrilUbicacionDTO> listaCarriles = new ArrayList<>();

    String sql = String.format("select LGNUM,LGTYP,LGPLA from HCMDB.dbo.ZPickingEntregaEntrante " +
        "WITH(NOLOCK) where EXIDV in('%s' ,'%s');", hu1, hu2);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

    for (Map<String, Object> row : rows) {
      CarrilUbicacionDTO carril = new CarrilUbicacionDTO();
      carril.setLgnum((String) row.get("LGNUM"));
      carril.setLgpla((String) row.get("LGPLA"));
      carril.setLgtyp((String) row.get("LGTYP"));
      listaCarriles.add(carril);
    }

    carriles.setItem(listaCarriles);
    return carriles;
  }

  @Override
  public ResultDTO rollBackPickingHU(String VBELN, String hu) {
    ResultDTO result = new ResultDTO();

    String sql = "update HCMDB.dbo.ZPickingEntregaEntrante set Status = 'X' where VBELN = ? and EXIDV = ?;";
    Object[] args = { VBELN, hu };

    result.setId(1);
    result.setMsg("HU CONFIRMADA EN UBICACION");

    jdbcTemplate.update(sql, args);
    result.setId(0);
    result.setMsg("NO FUE POSIBLE CONFIRMAR HU EN UBICACION");

    return result;
  }

  @Override
  public ResultDTO insertProcesoContingencia_3(EntregaInputDTO entrega, String hu) {
    ResultDTO result = new ResultDTO();

    String sql = "insert into HCMDB.dbo.zContingencia(IDPROC, FECHA, HORA, CENTRO, HU, ENTREGA, CONTROL_CALIDAD, " +
        "ALMACEN, USUARIO, TIPO_ALMACEN, DESTINO) select IDPROC = '%s' , convert(date,getdate()), " +
        "convert(time, getdate()), WERKS = '%s', HU ='%s',ENTREGA = '%s', CONTROL_CALIDAD = " +
        "(select DISTINCT(Clase_Inspeccion) from HCMDB.dbo.MaterialQUA as QA WITH(NOLOCK) where Centro= '%s' " +
        "and Material = '%s'),ALMACEN= '%s' , USUARIO= '%s', TIPO_ALMACEN='%s', DESTINO='%s';";

    Object[] args = { entrega.getLfart(), entrega.getWerks(), hu, entrega.getEntrega(), entrega.getWerks(),
        entrega.getMatnr(), entrega.getLgort(), entrega.getUsuarioMontacarga(), entrega.getuDestino1(),
        entrega.getuDestino2() };

    result.setId(0);
    result.setMsg("3to paso ejecutado con error");

    jdbcTemplate.update(sql, args);
    result.setId(1);
    result.setMsg("3to paso ejecutado con exito");

    return result;
  }

  @Override
  public ResultDTO aumentaInventario(EntregaInputDTO entrega, String hu) {
    ResultDTO result = new ResultDTO();

    String sql = "insert into HCMDB.dbo.LQUA (LGNUM, MATNR, WERKS, BESTQ, LGTYP, LGPLA, MEINS, GESME, VERME, " +
        "LENUM, LGORT, SKZUE, SKZUA, LETYP) VALUES ('%s', '%s', '%s', (select DISTINCT(Clase_Inspeccion) " +
        "from HCMDB.dbo.MaterialQUA as QA WITH(NOLOCK) where Centro= '%s' and Material= '%s'), '%s', '%s', " +
        "'%s', '%s', '%s', '%s', '%s',NULL,NULL, (select top (1) LETYP from ZPAITT_TTW WITH(NOLOCK) where " +
        "WERKS= '%s' and MATNR=(select top(1) VHILM FROM VEKP WITH(NOLOCK) where EXIDV= '%s')))";

    Object[] args = { entrega.getuDestino0(), entrega.getMatnr(), entrega.getWerks(), entrega.getWerks(),
        entrega.getMatnr(),
        entrega.getuDestino1(), entrega.getuDestino2(), entrega.getCantT(), entrega.getCant(), entrega.getCant(), hu,
        entrega.getLgort(), entrega.getWerks(), hu };

    jdbcTemplate.update(sql, args);
    result.setId(1);
    result.setMsg("Inventario aumentado");
    Utils.actualizarInventarioCarriles(entrega.getuDestino0(), entrega.getuDestino1(), entrega.getuDestino2());

    return result;
  }

  @Override
  public ResultDTO insertProcesoContingencia_4_14_32(String werks, String VBELN, String lfart, String user) {
    ResultDTO result = new ResultDTO();

    String sql = "insert into HCMDB.dbo.zContingencia(ENTREGA,IDPROC, FECHA, HORA, CENTRO, USUARIO) " +
        "values(?, ?, convert(date,getdate()), convert(time, getdate()), ?, ?)";
    Object[] args = { VBELN, lfart, werks, user };

    result.setId(0);
    result.setMsg("4to paso ejecutado con error");

    jdbcTemplate.update(sql, args);
    result.setId(1);
    result.setMsg("4to paso ejecutado con exito");

    return result;
  }

  @Override
  public EntregaInputDTO validarEntregaPickinCompleto(EntregaInputDTO entregaInput) {
    EntregaInputDTO entregaInputReturn = new EntregaInputDTO();
    ResultDTO result = new ResultDTO();
    HashMap<String, String> hashhMap = new HashMap<>();

    try {
      String sql = String.format(
          "select VBELN from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) where VBELN = '%s' " +
              "and WERKS = '%s' and Status is null",
          Utils.zeroFill(entregaInput.getEntrega(), 10), entregaInput.getWerks());
      Map<String, Object> row = jdbcTemplate.queryForMap(sql);
      result.setId(1);
      result.setMsg("ENTREGA ENTRANTE YA SE ENCUENTRA EN PICKING");
    } catch (Exception e) {
      e.printStackTrace();
      result.setId(0);
      result.setMsg("ENTREGA ENTRANTE NO DISPONIBLE PARA PICKING");
    }

    entregaInputReturn.setResultDT(result);
    entregaInputReturn.setMateriales(hashhMap);
    return entregaInputReturn;
  }
}
