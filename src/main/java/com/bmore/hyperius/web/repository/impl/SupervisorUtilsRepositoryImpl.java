package com.bmore.hyperius.web.repository.impl;

import java.io.File;
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
import com.bmore.hyperius.web.dto.CarrilesBloqueadosDTO;
import com.bmore.hyperius.web.dto.CarrilesBloqueadosDetalleDTO;
import com.bmore.hyperius.web.dto.CarrilesBloqueadosDetalleDTOItem;
import com.bmore.hyperius.web.dto.EmbarqueDTO;
import com.bmore.hyperius.web.dto.EmbarqueDetalleDTO;
import com.bmore.hyperius.web.dto.EmbarqueDetalleDTOItem;
import com.bmore.hyperius.web.dto.EntregasTransportesDTO;
import com.bmore.hyperius.web.dto.EntregasTransportesDetalleDTO;
import com.bmore.hyperius.web.dto.EntregasTransportesDetalleItemDTO;
import com.bmore.hyperius.web.dto.FTPConfDTO;
import com.bmore.hyperius.web.dto.HUsEnTransporteDetalleDTO;
import com.bmore.hyperius.web.dto.HusEnTransporteDTO;
import com.bmore.hyperius.web.dto.HusEnTransporteDetalleDTOItem;
import com.bmore.hyperius.web.dto.InventarioDTO;
import com.bmore.hyperius.web.dto.InventarioDetalleDTO;
import com.bmore.hyperius.web.dto.InventarioDetalleDTOItem;
import com.bmore.hyperius.web.dto.ListaDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.dto.UsuarioDTO;
import com.bmore.hyperius.web.dto.UsuarioItemDTO;
import com.bmore.hyperius.web.dto.UsuariosDTO;
import com.bmore.hyperius.web.repository.SupervisorUtilsRepository;
import com.bmore.hyperius.web.utils.Utils;

@Repository
public class SupervisorUtilsRepositoryImpl implements SupervisorUtilsRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  private final String pathOut = "E:" + File.separator + "RepoSentinel" + File.separator + "final";

  @Override
  public EntregasTransportesDTO obtieneEntrega(String tknum, String werks) {
    ResultDTO result = new ResultDTO();
    EntregasTransportesDTO entregasTransportesDTO = new EntregasTransportesDTO();
    EntregasTransportesDetalleItemDTO entregasTransportesDetalleDTOItem = new EntregasTransportesDetalleItemDTO();
    List<EntregasTransportesDetalleDTO> entregasTransportesDetalleDTOItemList = new ArrayList<>();

    try {
      String sql = String.format("select distinct(VTTP.VBELN),LIKP.LFART, " +
          "VTTP.TKNUM,zContingencia.ENTREGA AS CONTABILIZADA FROM VTTP WITH(NOLOCK) left join " +
          "HCMDB.dbo.zContingencia zContingencia WITH(NOLOCK) on VTTP.VBELN = zContingencia.ENTREGA " +
          "and (zContingencia.IDPROC='9' or zContingencia.IDPROC='4') inner join HCMDB.dbo.LIPS " +
          "LIPS with(nolock) on VTTP.VBELN = LIPS.VBELN inner join HCMDB.dbo.LIKP LIKP with(nolock) " +
          "on VTTP.VBELN = LIKP.VBELN where TKNUM= '%s' and LIPS.WERKS= '%s';", tknum, werks);
      Map<String, Object> row = jdbcTemplate.queryForMap(sql);

      EntregasTransportesDetalleDTO entregasTransportesDetalleDTO = new EntregasTransportesDetalleDTO();
      entregasTransportesDetalleDTO.setTknum((String) row.get("TKNUM"));
      entregasTransportesDetalleDTO.setVblenEntrante((String) row.get("VBELN"));
      entregasTransportesDetalleDTO.setStatus((String) row.get("CONTABILIZADA"));
      entregasTransportesDetalleDTO.setLfart((String) row.get("LFART"));
      result.setId(1);
      result.setMsg("Registro encontrado");
      entregasTransportesDetalleDTOItemList.add(entregasTransportesDetalleDTO);
      entregasTransportesDetalleDTOItem.setItem(entregasTransportesDetalleDTOItemList);
      entregasTransportesDTO.setItems(entregasTransportesDetalleDTOItem);
      entregasTransportesDTO.setResultDT(result);
    } catch (Exception e) {
      e.printStackTrace();
      result.setId(2);
      result.setMsg("Registro NO Encontrado");
    }

    return entregasTransportesDTO;
  }

  @Override
  public EntregasTransportesDTO obtieneEntregas(String werks) {
    ResultDTO result = new ResultDTO();

    EntregasTransportesDTO entregasTransportesDTO = new EntregasTransportesDTO();
    EntregasTransportesDetalleItemDTO entregasTransportesDetalleDTOItem = new EntregasTransportesDetalleItemDTO();
    List<EntregasTransportesDetalleDTO> entregasTransportesDetalleDTOItemList = new ArrayList<>();

    result.setId(2);
    result.setMsg("Registros no encontrados");

    String sql = String.format("SELECT VBELN, LFART, TKNUM, CONTABILIZADA, EDI FROM " +
        "VS_BCPS_SUPERVISOR_UTILS_ENTREGAS WITH(NOLOCK) WHERE WERKS = '%s';", werks);
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

    for (Map<String, Object> row : rows) {
      EntregasTransportesDetalleDTO entregasTransportesDetalleDTO = new EntregasTransportesDetalleDTO();
      entregasTransportesDetalleDTO.setTknum((String) row.get("TKNUM"));
      entregasTransportesDetalleDTO.setVblenEntrante((String) row.get("VBELN"));
      entregasTransportesDetalleDTO.setStatus((String) row.get("CONTABILIZADA"));
      entregasTransportesDetalleDTO.setLfart((String) row.get("LFART"));
      entregasTransportesDetalleDTO.setEdi((String) row.get("EDI"));
      result.setId(1);
      result.setMsg("Registro encontrado");
      entregasTransportesDetalleDTOItemList.add(entregasTransportesDetalleDTO);
    }

    entregasTransportesDetalleDTOItem.setItem(entregasTransportesDetalleDTOItemList);
    entregasTransportesDTO.setItems(entregasTransportesDetalleDTOItem);
    entregasTransportesDTO.setResultDT(result);

    return entregasTransportesDTO;
  }

  @Override
  public InventarioDTO obtieneInventario(String werks) {
    ResultDTO result = new ResultDTO();

    InventarioDTO inventarioDTO = new InventarioDTO();
    InventarioDetalleDTOItem inventarioDetalleDTOItem = new InventarioDetalleDTOItem();
    List<InventarioDetalleDTO> inventarioDetalleDTOItemList = new ArrayList<>();

    result.setId(2);
    result.setMsg("Registros no encontrados");

    String sql = String.format("select MATNR,VERME,COUNT(MATNR) as CANTIDAD_HUS, " +
        "LGNUM,LGTYP,LGPLA from HCMDB.dbo.LQUA WITH(NOLOCK) where WERKS= '%s' and SKZUA " +
        "is null group by MATNR, VERME, LGNUM, LGTYP, LGPLA order by MATNR;", werks);

    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    for (Map<String, Object> row : rows) {
      InventarioDetalleDTO inventarioDetalleDTO = new InventarioDetalleDTO();
      inventarioDetalleDTO.setMatnr((String) row.get("MATNR"));
      inventarioDetalleDTO.setVerme((String) row.get("VERME"));
      inventarioDetalleDTO.setCantidadHus((String) row.get("CANTIDAD_HUS"));
      inventarioDetalleDTO.setLgnum((String) row.get("LGNUM"));
      inventarioDetalleDTO.setLgtyp((String) row.get("LGTYP"));
      inventarioDetalleDTO.setLgpla((String) row.get("LGPLA"));

      result.setId(1);
      result.setMsg("Registro encontrado");
      inventarioDetalleDTOItemList.add(inventarioDetalleDTO);
    }

    inventarioDetalleDTOItem.setItem(inventarioDetalleDTOItemList);
    inventarioDTO.setItems(inventarioDetalleDTOItem);
    inventarioDTO.setResultDT(result);

    return inventarioDTO;
  }

  @Override
  public InventarioDTO obtieneInventarioLotes(String werks) {
    ResultDTO result = new ResultDTO();
    InventarioDTO inventarioDTO = new InventarioDTO();
    InventarioDetalleDTOItem inventarioDetalleDTOItem = new InventarioDetalleDTOItem();
    List<InventarioDetalleDTO> inventarioDetalleDTOItemList = new ArrayList<>();

    result.setId(2);
    result.setMsg("Registros no encontrados");

    String sql = String.format("select MATNR,VERME,COUNT(MATNR) as CANTIDAD_HUS, LGNUM, " +
        "LGTYP, LGPLA, CHARG from HCMDB.dbo.LQUA WITH(NOLOCK) where WERKS= '%s' and SKZUA " +
        "is null group by MATNR, VERME, LGNUM, LGTYP, LGPLA,CHARG order by MATNR;", werks);

    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    for (Map<String, Object> row : rows) {
      InventarioDetalleDTO inventarioDetalleDTO = new InventarioDetalleDTO();
      inventarioDetalleDTO.setMatnr((String) row.get("MATNR"));
      inventarioDetalleDTO.setVerme((String) row.get("VERME"));
      inventarioDetalleDTO.setCantidadHus((String) row.get("CANTIDAD_HUS"));
      inventarioDetalleDTO.setLgnum((String) row.get("LGNUM"));
      inventarioDetalleDTO.setLgtyp((String) row.get("LGTYP"));
      inventarioDetalleDTO.setLgpla((String) row.get("LGPLA"));
      inventarioDetalleDTO.setCharg((String) row.get("CHARG"));
      result.setId(1);
      result.setMsg("Registro encontrado");
      inventarioDetalleDTOItemList.add(inventarioDetalleDTO);
    }

    inventarioDetalleDTOItem.setItem(inventarioDetalleDTOItemList);
    inventarioDTO.setItems(inventarioDetalleDTOItem);
    inventarioDTO.setResultDT(result);
    return inventarioDTO;
  }

  @Override
  public HusEnTransporteDTO obtieneCarrilesBloqueados(String proceso, String vbeln, String werks) {
    ResultDTO result = new ResultDTO();

    HusEnTransporteDTO husEnTransporteDTO = new HusEnTransporteDTO();
    HusEnTransporteDetalleDTOItem husEnTransporteDetalleDTOItem = new HusEnTransporteDetalleDTOItem();
    List<HUsEnTransporteDetalleDTO> husEnTransporteDetalleDTOItemList = new ArrayList<>();

    result.setId(2);
    result.setMsg("No hay carriles bloqueados");

    String sql = String.format("SELECT DISTINCT LGNUM, LGTYP, LGPLA, husTransporte = (SELECT COUNT(*) " +
        "FROM ZPickingEntregaEntrante WITH(NOLOCK) WHERE idProceso = ZPEE.idProceso " +
        "AND werks = ZPEE.werks AND VBELN = ZPEE.VBELN AND LGNUM=ZPEE.LGNUM AND " +
        "LGTYP=ZPEE.LGTYP AND LGPLA=ZPEE.LGPLA AND (status = '1' or (EXIDV is not null " +
        "and status is null))), husAsignadas = (SELECT COUNT(*) FROM ZPickingEntregaEntrante " +
        "WITH(NOLOCK) WHERE idProceso = ZPEE.idProceso AND werks = ZPEE.werks AND VBELN = " +
        "ZPEE.VBELN AND LGNUM=ZPEE.LGNUM AND LGTYP=ZPEE.LGTYP AND LGPLA=ZPEE.LGPLA) FROM " +
        "ZPickingEntregaEntrante ZPEE WITH(NOLOCK) WHERE idProceso = '%s'  AND VBELN = '%s' AND " +
        "werks = '%s' and (Status is null or Status ='1') order by LGNUM,LGTYP,LGPLA;", proceso, vbeln, werks);

    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    for (Map<String, Object> row : rows) {
      HUsEnTransporteDetalleDTO husEnTransporteDetalleDTO = new HUsEnTransporteDetalleDTO();
      husEnTransporteDetalleDTO.setLgnum((String) row.get("LGNUM"));
      husEnTransporteDetalleDTO.setLgtyp((String) row.get("LGTYP"));
      husEnTransporteDetalleDTO.setLgpla((String) row.get("LGPLA"));
      husEnTransporteDetalleDTO.setHusAsignadas((String) row.get("husAsignadas"));
      husEnTransporteDetalleDTO.setHusEnTransporte((String) row.get("husTransporte"));
      husEnTransporteDetalleDTO.setVbeln(vbeln);
      result.setId(1);
      result.setMsg("Registro encontrado");
      husEnTransporteDetalleDTOItemList.add(husEnTransporteDetalleDTO);
    }

    husEnTransporteDetalleDTOItem.setItem(husEnTransporteDetalleDTOItemList);
    husEnTransporteDTO.setItems(husEnTransporteDetalleDTOItem);
    husEnTransporteDTO.setResultDT(result);

    return husEnTransporteDTO;
  }

  @Override
  public HusEnTransporteDTO obtieneMaterialesBloqueados(String proceso, String vbeln, String werks) {
    ResultDTO result = new ResultDTO();

    HusEnTransporteDTO husEnTransporteDTO = new HusEnTransporteDTO();
    HusEnTransporteDetalleDTOItem husEnTransporteDetalleDTOItem = new HusEnTransporteDetalleDTOItem();
    List<HUsEnTransporteDetalleDTO> husEnTransporteDetalleDTOItemList = new ArrayList<>();

    result.setId(2);
    result.setMsg("No hay carriles bloqueados");

    String sql = String.format("select distinct MATNR, husTransporte=(SELECT COUNT(*) FROM ZPickingVidrio " +
        "WITH(NOLOCK) WHERE idProceso = ZPEE.idProceso AND werks = ZPEE.werks  AND " +
        "VBELN = ZPEE.VBELN AND status = '1'), husAsignadas=(SELECT COUNT(*) FROM " +
        "ZPickingVidrio WITH(NOLOCK) WHERE idProceso = ZPEE.idProceso AND werks = ZPEE.werks " +
        "AND VBELN = ZPEE.VBELN ) from ZPickingVidrio ZPEE with(nolock) where VBELN = '%s' " +
        "and WERKS = '%s' and idProceso = '%s' and (Status is null or Status ='1') order by MATNR;", vbeln, werks,
        proceso);

    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    for (Map<String, Object> row : rows) {
      HUsEnTransporteDetalleDTO husEnTransporteDetalleDTO = new HUsEnTransporteDetalleDTO();

      husEnTransporteDetalleDTO.setMatnr((String) row.get("MATNR"));
      husEnTransporteDetalleDTO.setHusAsignadas((String) row.get("husAsignadas"));
      husEnTransporteDetalleDTO.setHusEnTransporte((String) row.get("husTransporte"));

      result.setId(1);
      result.setMsg("Registro encontrado");
      husEnTransporteDetalleDTOItemList.add(husEnTransporteDetalleDTO);
    }

    husEnTransporteDetalleDTOItem.setItem(husEnTransporteDetalleDTOItemList);
    husEnTransporteDTO.setItems(husEnTransporteDetalleDTOItem);
    husEnTransporteDTO.setResultDT(result);

    return husEnTransporteDTO;
  }

  @Override
  public ResultDTO limpiaCarril(CarrilUbicacionDTO carril) {
    ResultDTO result = new ResultDTO();
    String sql = "exec sp_bcps_wm_libera_pendientes_carril ?, ?, ?, ?, ?, ?, ?, ?;";
    Object[] args = { carril.getEntrega(), carril.getIdProceso(), carril.getLgnum(), carril.getLgtyp(),
        carril.getLgpla(), carril.getMaterial(), carril.getTipoAlmacen(), java.sql.Types.INTEGER };

    Integer id = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getInt(8), args);
    assert id != null;
    result.setId(id);
    return result;
  }

  @Override
  public HusEnTransporteDTO obtieneHusBloqueados(HUsEnTransporteDetalleDTO husEnTransporteDetalleDTO, String werks) {

    ResultDTO result = new ResultDTO();
    HusEnTransporteDTO husEnTransporteDTO = new HusEnTransporteDTO();
    HusEnTransporteDetalleDTOItem husEnTransporteDetalleDTOItems = new HusEnTransporteDetalleDTOItem();
    List<HUsEnTransporteDetalleDTO> husEnTransporteDetalleDTOItemList = new ArrayList<>();
    result.setId(2);
    result.setMsg("No hay hus en transporte");

    String sql = String.format("select EXIDV, Status, usuarioMontacarga, matnr from ZPickingEntregaEntrante " +
        "with(nolock) where VBELN = '%s' and idProceso = '%s' and werks = '%s' and LGNUM= '%s' AND " +
        "LGTYP= '%s' AND LGPLA= '%s' order by Status desc;", husEnTransporteDetalleDTO.getVbeln(),
        husEnTransporteDetalleDTO.getIdProceso(), werks, husEnTransporteDetalleDTO.getLgnum(),
        husEnTransporteDetalleDTO.getLgtyp(), husEnTransporteDetalleDTO.getLgpla());

    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

    for (Map<String, Object> row : rows) {
      HUsEnTransporteDetalleDTO husEnTransporteDetalleDTOItem = new HUsEnTransporteDetalleDTO();
      husEnTransporteDetalleDTOItem.setHu((String) row.get("exidv"));
      husEnTransporteDetalleDTOItem.setStatus((String) row.get("status"));
      husEnTransporteDetalleDTOItem.setUsuarioMontacarguista((String) row.get("usuarioMontacarga"));
      husEnTransporteDetalleDTOItem.setMatnr((String) row.get("matnr"));
      husEnTransporteDetalleDTOItem.setVbeln(husEnTransporteDetalleDTO.getVbeln());
      result.setId(1);
      result.setMsg("Registro encontrado");
      husEnTransporteDetalleDTOItemList.add(husEnTransporteDetalleDTOItem);
    }

    husEnTransporteDetalleDTOItems.setItem(husEnTransporteDetalleDTOItemList);
    husEnTransporteDTO.setItems(husEnTransporteDetalleDTOItems);
    husEnTransporteDTO.setResultDT(result);

    return husEnTransporteDTO;
  }

  @Override
  public HusEnTransporteDTO obtieneHusIMBloqueados(HUsEnTransporteDetalleDTO husEnTransporteDetalleDTO, String werks) {

    ResultDTO result = new ResultDTO();
    HusEnTransporteDTO husEnTransporteDTO = new HusEnTransporteDTO();
    HusEnTransporteDetalleDTOItem husEnTransporteDetalleDTOItems = new HusEnTransporteDetalleDTOItem();
    List<HUsEnTransporteDetalleDTO> husEnTransporteDetalleDTOItemList = new ArrayList<>();

    String sql = String.format("select EXIDV, Status, usuarioMontacarga, matnr from ZPickingVidrio " +
        "with(nolock) where VBELN = '%s' and idProceso = '%s' and werks= '%s' and MATNR= '%s' " +
        "order by Status desc;", husEnTransporteDetalleDTO.getVbeln(), husEnTransporteDetalleDTO.getIdProceso(), werks,
        husEnTransporteDetalleDTO.getMatnr());
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

    result.setId(2);
    result.setMsg("No hay materiales bloqueados");

    for (Map<String, Object> row : rows) {
      HUsEnTransporteDetalleDTO husEnTransporteDetalleDTOItem = new HUsEnTransporteDetalleDTO();
      husEnTransporteDetalleDTOItem.setHu((String) row.get("exidv"));
      husEnTransporteDetalleDTOItem.setStatus((String) row.get("status"));
      husEnTransporteDetalleDTOItem.setUsuarioMontacarguista((String) row.get("usuarioMontacarga"));
      husEnTransporteDetalleDTOItem.setMatnr((String) row.get("matnr"));
      husEnTransporteDetalleDTOItem.setVbeln(husEnTransporteDetalleDTO.getVbeln());
      result.setId(1);
      result.setMsg("Registro encontrado");
      husEnTransporteDetalleDTOItemList.add(husEnTransporteDetalleDTOItem);
    }

    husEnTransporteDetalleDTOItems.setItem(husEnTransporteDetalleDTOItemList);
    husEnTransporteDTO.setItems(husEnTransporteDetalleDTOItems);
    husEnTransporteDTO.setResultDT(result);

    return husEnTransporteDTO;
  }

  @Override
  public ResultDTO validarCarril(CarrilUbicacionDTO carril) {
    ResultDTO resultDT = new ResultDTO();

    String sql = String.format("SELECT DISTINCT LAGP.LGNUM, ZPEE.IDPROCESO, ZPEE.USUARIOSUPERVISOR, " +
        "ZUSUARIO.NOMBRE FROM LAGP LAGP WITH(NOLOCK) LEFT JOIN ZPICKINGENTREGAENTRANTE " +
        "ZPEE WITH(NOLOCK) ON LAGP.LGNUM = ZPEE.LGNUM AND LAGP.LGTYP = ZPEE.LGTYP AND " +
        "LAGP.LGPLA = ZPEE.LGPLA AND (STATUS IS NULL OR STATUS ='1') LEFT JOIN ZUSUARIO " +
        "ZUSUARIO WITH(NOLOCK) ON ZPEE.USUARIOSUPERVISOR = ZUSUARIO.IDRED WHERE " +
        "LAGP.LGNUM = '%s' AND LAGP.LGTYP = '%s' AND LAGP.LGPLA = '%s';", carril.getLgnum(),
        carril.getLgtyp(), carril.getLgpla());

    try {
      Map<String, Object> row = jdbcTemplate.queryForMap(sql);
      String id_proceso = (String) row.get("idProceso");

      if (id_proceso == null || id_proceso.equals(carril.getIdProceso())) {
        resultDT.setId(1);
        resultDT.setMsg("El carril existe.");
      } else {
        resultDT.setId(3);
        // resultDT
        // .setMsg("Existe carril, pero está siendo utilizado por otro proceso, elija
        // otro. Usuario: "
        // + rs.getString("usuarioSupervisor")
        // + " - "
        // + rs.getString("nombre"));

        resultDT
            .setMsg("El carril esta siendo utilizado por el usuario: "
                + row.get("usuarioSupervisor")
                + " - "
                + row.get("nombre"));
      }

    } catch (Exception e) {
      e.printStackTrace();
      resultDT.setId(2);
      resultDT.setMsg("El carril no existe");
    }

    return resultDT;
  }

  @Override
  public ListaDTO getCentros() {
    ListaDTO listaDTO = new ListaDTO();
    List<String> centros = new ArrayList<>();
    ResultDTO resultDT = new ResultDTO();

    String sql = "SELECT DISTINCT WERKS FROM ZCENTROSBCPS WITH(NOLOCK)";
    resultDT.setId(0);
    resultDT.setMsg("No fue posible recuperar los centros");

    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    for (Map<String, Object> row : rows) {
      centros.add((String) row.get("WERKS"));
      resultDT.setId(1);
      resultDT.setMsg("Centros recuperados con exito");
    }

    listaDTO.setLista(centros);
    listaDTO.setResultDT(resultDT);

    return listaDTO;
  }

  @Override
  public FTPConfDTO getFTPConf() {
    FTPConfDTO ftpConf = new FTPConfDTO();
    ResultDTO resultDT = new ResultDTO();

    resultDT.setId(0);
    resultDT.setMsg("No fue posible recuper la configuración del FTP.");

    try {
      String sql = "select IP,PORT,[USER],[PASSWORD],FOLDER from  TB_BCPS_SFTP_DATAMART with(nolock)";
      Map<String, Object> row = jdbcTemplate.queryForMap(sql);

      ftpConf.setIp((String) row.get("IP"));
      ftpConf.setPassword((String) row.get("PASSWORD"));
      ftpConf.setPuerto((String) row.get("PORT"));
      ftpConf.setUser((String) row.get("USER"));
      ftpConf.setFolder((String) row.get("FOLDER"));

      resultDT.setId(1);
      resultDT.setMsg("Configuración FTP recuperada con exito.");
    } catch (Exception e) {
      e.printStackTrace();
    }

    ftpConf.setResultDT(resultDT);
    return ftpConf;
  }

  @Override
  public ListaDTO getTablas(String werks) {
    ListaDTO listaDTO = new ListaDTO();
    List<String> centros = new ArrayList<>();
    ResultDTO resultDT = new ResultDTO();

    String sql = "select NOMBRE_TABLA, CERVECERAS, ENVASES, VIDRIERAS from TB_BCPS_TABLE_NAME " +
        "with(nolock) order by  NOMBRE_TABLA";

    resultDT.setId(0);
    resultDT.setMsg("No fue posible recuperar las tablas");

    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

    for (Map<String, Object> row : rows) {
      if ((werks.startsWith("PC") || (werks.startsWith("PA"))) && (Boolean) row.get("CERVECERAS")) {
        centros.add((String) row.get("NOMBRE_TABLA"));
        resultDT.setId(1);
        resultDT.setMsg("Las tablas fueron recuperadas con éxito");
      } else if (werks.startsWith("PV") && (Boolean) row.get("VIDRIERAS")) {
        centros.add((String) row.get("NOMBRE_TABLA"));
        resultDT.setId(1);
        resultDT.setMsg("Las tablas fueron recuperadas con éxito");
      } else if ((werks.startsWith("EM") || werks.startsWith("TM")) && (Boolean) row.get("ENVASES")) {
        centros.add((String) row.get("NOMBRE_TABLA"));
        resultDT.setId(1);
        resultDT.setMsg("Las tablas fueron recuperadas con éxito");
      }
    }

    listaDTO.setLista(centros);
    listaDTO.setResultDT(resultDT);
    return listaDTO;
  }

  @Override
  public ResultDTO bulk(String tabla, String werks) {
    ResultDTO resultDT = new ResultDTO();

    String tablaSQL;
    String txtTabla;
    String campo;

    switch (tabla) {
      case "ZPAITT_CONT_HU": {
        tablaSQL = "TB_BCPS_NEW_HU";
        txtTabla = tabla;
        break;
      }
      case "ZPAITT_FACTURA": {
        tablaSQL = "TB_BCPS_ZFACT";
        txtTabla = tabla;
        break;
      }
      case "ZPAITT_ENTREGA": {
        tablaSQL = "TB_BCPS_NEW_VBELN";
        txtTabla = tabla;
        break;
      }
      case "ZPAITT_HU": {
        tablaSQL = "ZPAITT_HU_EXT";
        txtTabla = tabla;
        break;
      }
      case "ZPAITT_PALL_EYT": {
        tablaSQL = "ZPAITT_PALLETOBR";
        txtTabla = tabla;
        break;
      }
      default: {
        tablaSQL = tabla;
        txtTabla = tablaSQL;
        break;
      }
    }

    String sql_1 = "exec SP_BCPS_GET_OBJECT_ID ?, ?, ?;";
    Object[] args = { tablaSQL, java.sql.Types.VARCHAR, java.sql.Types.INTEGER };

    Map<Integer, Object> row = jdbcTemplate.queryForObject(sql_1, args, (rs, rowNum) -> {
      Map<Integer, Object> map = new HashMap<>();
      map.put(1, rs.getInt(1));
      map.put(2, rs.getInt(2));
      map.put(3, rs.getInt(3));
      return map;
    });

    assert row != null;
    if ((Integer) row.get(1) == 1) {
      campo = (String) row.get(2);

      String sql_2 = "bulk insert "
          + tablaSQL
          + " from  '" + pathOut + "_"
          + txtTabla
          + "_"
          + werks
          + ".txt' "
          + "with (FIELDTERMINATOR = '|-|', KEEPIDENTITY, KEEPNULLS, ROWTERMINATOR ='\n', FIRSTROW=2, CODEPAGE = 'ACP')";
      jdbcTemplate.update(sql_2);

      if (campo.equals("SKZUA") || campo.equals("HU_LGORT")) {
        String sql_3 = "update HCMDB.dbo."
            + tablaSQL + " set " + campo
            + " = case when len(" + campo
            + ") > 1 then replace(" + campo
            + ",char(13),'') else replace(" + campo
            + ",char(13),null) end  where " + campo
            + " NOT IN ('A','X') and " + campo
            + " is not null";
        jdbcTemplate.update(sql_3);
      } else {
        String sql_3 = "update HCMDB.dbo."
            + tablaSQL + " set " + campo
            + " = case when len(" + campo + ") > 1"
            + " then replace(" + campo
            + ",char(13),'') else replace(" + campo
            + ",char(13),null) end";
        jdbcTemplate.update(sql_3);
      }
      log.info("Tabla SQL: " + tablaSQL + "   TxtTabla: " + txtTabla + "  Centro:" + werks);
      resultDT.setId(1);
    } else {
      resultDT.setId(2);
      resultDT.setMsg("No se recupero el ultimo campo de la tabla:" + tabla);
    }

    return resultDT;
  }

  @Override
  public void removeQuality() {
    String sql = "exec SP_BCPS_BLOCK_QUALITY";
    jdbcTemplate.execute(sql);
  }

  @Override
  public ResultDTO eliminaDuplicados() {
    ResultDTO result = new ResultDTO();
    String sql = "exec sp_bcps_utils_elimina_duplicados_v2";
    jdbcTemplate.execute(sql);
    result.setId(1);
    return result;
  }

  @Override
  public ResultDTO validaInicioBCPS(String werks) {
    ResultDTO result = new ResultDTO();

    try {
      String sql = String.format("select top 1 * from zContingencia with(nolock) where CENTRO = '%s';", werks);
      Map<String, Object> row = jdbcTemplate.queryForMap(sql);
      result.setId(2);
      result.setMsg("BCPS inicio operaciones con el centro: " + werks + ", ya no es posible cargar datos.");
    } catch (Exception e) {
      e.printStackTrace();
      result.setId(1);
    }

    return result;
  }

  @Override
  public UsuarioItemDTO buscarUsuario(String idUser) {

    ResultDTO result = new ResultDTO();
    UsuarioItemDTO usuariItemDTO = new UsuarioItemDTO();
    UsuariosDTO usuariosDTO = new UsuariosDTO();
    List<UsuarioDTO> listaUsuarios = new ArrayList<>();

    try {
      String sql = String.format("SELECT ZUSR.NOMBRE, ZUSR.WERKS, ZCBP.descripcion from ZUSUARIO ZUSR " +
          "WITH(NOLOCK) INNER JOIN zCentrosBCPS ZCBP WITH(NOLOCK) ON ZUSR.WERKS = ZCBP.werks " +
          "where idRed = '%s';", idUser);
      Map<String, Object> row = jdbcTemplate.queryForMap(sql);

      UsuarioDTO usuarioDTO = new UsuarioDTO();
      usuarioDTO.setIdUsuario(idUser);
      usuarioDTO.setName((String) row.get("NOMBRE"));
      usuarioDTO.setWerks((String) row.get("WERKS"));
      usuarioDTO.setDescWerks((String) row.get("descripcion"));
      listaUsuarios.add(usuarioDTO);

      result.setId(1);
      result.setMsg("Usuario encontrado");
    } catch (Exception e) {
      e.printStackTrace();
      result.setId(2);
      result.setMsg("Usuario no encontrado.");
    }

    usuariItemDTO.setResult(result);
    usuariosDTO.setItem(listaUsuarios);
    usuariItemDTO.setItem(usuariosDTO);

    return usuariItemDTO;
  }

  @Override
  public ResultDTO eliminarUsuario(String idUser) {
    ResultDTO result = new ResultDTO();
    String sql = "exec sp_bcps_utils_valid_user ?, ?, ?, ?, ?, ?";
    Object[] args = { idUser, "", "", "", 1, java.sql.Types.INTEGER };

    Integer id = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getInt(6), args);
    assert id != null;
    result.setId(id);
    return result;
  }

  @Override
  public ResultDTO crearUsuario(UsuarioDTO user) {
    ResultDTO result = new ResultDTO();
    String sql = "exec sp_bcps_utils_valid_user ?, ?, ?, ?, ?, ?";
    Object[] args = { user.getIdUsuario(), user.getWerks(), user.getName(), "", 0, java.sql.Types.INTEGER };

    Integer id = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getInt(6), args);
    assert id != null;
    result.setId(id);
    return result;
  }

  @Override
  public ResultDTO modificarUsuario(UsuarioDTO user) {
    ResultDTO result = new ResultDTO();
    String sql = "exec sp_bcps_utils_valid_user ?, ?, ?, ?, ?, ?";
    Object[] args = { user.getIdUsuario(), user.getWerks(), user.getName(), "", 0, java.sql.Types.INTEGER };

    Integer id = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getInt(6), args);
    assert id != null;
    result.setId(id);
    return result;
  }

  @Override
  public ResultDTO limpiaTablasCentro(String werks) {
    ResultDTO result = new ResultDTO();
    String sql = "exec sp_bcps_utils_delete_data_by_werks ?, ?";
    Object[] args = { werks, java.sql.Types.INTEGER };

    Integer id = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getInt(2), args);
    assert id != null;
    result.setId(id);
    return result;
  }

  @Override
  public EntregasTransportesDTO obtieneEntregasAgencias(UsuarioDTO usuario) {
    ResultDTO result = new ResultDTO();
    EntregasTransportesDTO entregasTransportesDTO = new EntregasTransportesDTO();
    EntregasTransportesDetalleItemDTO entregasTransportesDetalleDTOItem = new EntregasTransportesDetalleItemDTO();
    List<EntregasTransportesDetalleDTO> entregasTransportesDetalleDTOItemList = new ArrayList<EntregasTransportesDetalleDTO>();

    result.setId(2);
    result.setMsg("Registros no encontrados");

    String sql = String.format("SELECT TKNUM, VBELN, VBELV FROM VS_BCPS_ENTREGAS_AGENCIAS " +
        "WITH(NOLOCK) WHERE WERKS = '%s'", usuario.getWerks());
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

    for (Map<String, Object> row : rows) {
      EntregasTransportesDetalleDTO entregasTransportesDetalleDTO = new EntregasTransportesDetalleDTO();

      entregasTransportesDetalleDTO.setTknum((String) row.get("TKNUM"));
      entregasTransportesDetalleDTO.setVblenEntrante((String) row.get("VBELN"));
      entregasTransportesDetalleDTO.setVblenSaliente((String) row.get("VBELV"));

      result.setId(1);
      result.setMsg("Registro encontrado");
      entregasTransportesDetalleDTOItemList.add(entregasTransportesDetalleDTO);
    }

    entregasTransportesDetalleDTOItem.setItem(entregasTransportesDetalleDTOItemList);
    entregasTransportesDTO.setItems(entregasTransportesDetalleDTOItem);
    entregasTransportesDTO.setResultDT(result);
    return entregasTransportesDTO;
  }

  @Override
  public CarrilesBloqueadosDTO obtieneCarrilesBloqueados(UsuarioDTO usuario) {
    ResultDTO result = new ResultDTO();

    CarrilesBloqueadosDTO carrilesBloqueadosDTO = new CarrilesBloqueadosDTO();
    CarrilesBloqueadosDetalleDTOItem carrilesBloqueadosDetalleDTOItem = new CarrilesBloqueadosDetalleDTOItem();
    List<CarrilesBloqueadosDetalleDTO> carrilesBloqueadosDetalleDTOItemList = new ArrayList<>();

    result.setId(2);
    result.setMsg("Registros no encontrados");

    String sql = String.format("SELECT VBELN, LGNUM, LGTYP, LGPLA, NOMBRE, PROCESO, MARCA_TIEMPO FROM " +
        "VS_BCPS_UTILS_BLOQUEO WHERE werks = '%s';", usuario.getWerks());

    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

    for (Map<String, Object> row : rows) {
      CarrilesBloqueadosDetalleDTO carrilesBloqueadosDetalleDTO = new CarrilesBloqueadosDetalleDTO();
      carrilesBloqueadosDetalleDTO.setVbeln((String) row.get("VBELN"));
      carrilesBloqueadosDetalleDTO.setCarrilBloqueado(Utils.isNull((String) row.get("LGNUM")) + " "
          + Utils.isNull((String) row.get("LGTYP")) + " " + Utils.isNull((String) row.get("LGPLA")));
      carrilesBloqueadosDetalleDTO.setUser((String) row.get("NOMBRE"));
      carrilesBloqueadosDetalleDTO.setProceso((String) row.get("PROCESO"));
      carrilesBloqueadosDetalleDTO.setMarcaTiempo((String) row.get("MARCA_TIEMPO"));
      result.setId(1);
      result.setMsg("Registro encontrado");
      carrilesBloqueadosDetalleDTOItemList.add(carrilesBloqueadosDetalleDTO);
    }

    carrilesBloqueadosDetalleDTOItem.setItem(carrilesBloqueadosDetalleDTOItemList);
    carrilesBloqueadosDTO.setItems(carrilesBloqueadosDetalleDTOItem);
    carrilesBloqueadosDTO.setResultDT(result);

    return carrilesBloqueadosDTO;
  }

  @Override
  public EmbarqueDTO obtieneEntregasAgenciasDetalle(String vbeln) {
    ResultDTO result = new ResultDTO();
    EmbarqueDetalleDTOItem embarqueDetalleDTOItem = new EmbarqueDetalleDTOItem();
    List<EmbarqueDetalleDTO> items = new ArrayList<>();
    EmbarqueDTO embarque = new EmbarqueDTO();

    String sql_1 = String.format("select POSNR, MATNR, ARKTX, PSTYV from HCMDB.dbo.LIPS with(nolock) " +
        "where VBELN= '%s' and PSTYV in (select tipo_posicion FROM TB_BCPS_TIPO_DOC_POS " +
        "where ID_PROC = 1) and convert(decimal(18, 3), LFIMG) > 0", vbeln);
    List<Map<String, Object>> rows_1 = jdbcTemplate.queryForList(sql_1);
    HashMap<String, String> map = new HashMap<>();

    for (Map<String, Object> row_1 : rows_1) {
      String matnr = (String) row_1.get("MATNR");

      if (map.get(matnr) == null) {
        map.put(matnr, matnr);
        EmbarqueDetalleDTO item = new EmbarqueDetalleDTO();
        item.setMaterial(matnr);
        item.setPosicion((String) row_1.get("POSNR"));
        item.setDescripcion((String) row_1.get("ARKTX"));

        try {
          String sql_2 = String.format("select sum(convert(decimal(18, 3), LFIMG)) as sum, MEINS from " +
              "HCMDB.dbo.lips WITH(NOLOCK) where VBELN = '%s' and MATNR = '%s' and PSTYV " +
              "in (select tipo_posicion FROM TB_BCPS_TIPO_DOC_POS where ID_PROC = 1) " +
              "and convert(decimal(18, 3), LFIMG) > 0 group by MEINS;", vbeln, item.getMaterial());
          Map<String, Object> row_2 = jdbcTemplate.queryForMap(sql_2);
          try {
            item.setCajas(
                String.valueOf(BigDecimal.valueOf((Double) row_2.get("sum")).setScale(3, RoundingMode.HALF_UP)));
          } catch (Exception ex) {
            ex.printStackTrace();
            item.setCajas((String) row_2.get("sum"));
          }
          item.setMe((String) row_2.get("MEINS"));
        } catch (Exception e) {
          e.printStackTrace();
          item.setCajas("0");
        }
        items.add(item);
      }
    }

    log.info("Size Lista " + items.size());
    if (items.size() > 0) {
      result.setId(1);
      result.setMsg("Detalle de entrega encontrado");
    } else {
      result.setId(2);
      result.setMsg("Detalle de entrega NO encontrado");
    }

    embarqueDetalleDTOItem.setItem(items);
    embarque.setItems(embarqueDetalleDTOItem);
    embarque.setResultDT(result);
    return embarque;
  }

  @Override
  public ResultDTO initialSnapshot(String werks) {
    ResultDTO result = new ResultDTO();
    String sql = String.format("exec SP_BCPS_INITIAL_SNAPSHOT '%s'", werks);
    jdbcTemplate.execute(sql);
    result.setId(1);
    return result;
  }
}
