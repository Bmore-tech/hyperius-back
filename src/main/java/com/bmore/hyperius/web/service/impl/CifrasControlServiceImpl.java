package com.bmore.hyperius.web.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bmore.hyperius.web.dto.CargaInformacionBodyDTO;
import com.bmore.hyperius.web.dto.CargaInformacionDTO;
import com.bmore.hyperius.web.dto.DescargaInformacionDTO;
import com.bmore.hyperius.web.dto.PlaneacionBodyDTO;
import com.bmore.hyperius.web.dto.ReporteAvanceBodyDTO;
import com.bmore.hyperius.web.dto.ReporteAvanceDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.CifrasControlRepository;
import com.bmore.hyperius.web.service.CifrasControlService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CifrasControlServiceImpl implements CifrasControlService {

  @Autowired
  private CifrasControlRepository cifrasControlRepository;

  @Override
  public DescargaInformacionDTO getDescargaInformacion() {
    DescargaInformacionDTO descargaInformacionDTO = new DescargaInformacionDTO();
    // CifrasControlRepositoryOld cifrasControlDAO = new
    // CifrasControlRepositoryOld();
    descargaInformacionDTO = cifrasControlRepository.getDescargaInformacion();
    if (descargaInformacionDTO.getResultDT().getId() == 1) {
      Date date = new Date();
      SimpleDateFormat sda = new SimpleDateFormat("yyyy");
      SimpleDateFormat sdm = new SimpleDateFormat("MM");
      SimpleDateFormat sdd = new SimpleDateFormat("dd");
      descargaInformacionDTO.setAnio(sda.format(date));
      descargaInformacionDTO.setMes(sdm.format(date));
      descargaInformacionDTO.setDia(sdd.format(date));
    }

    return descargaInformacionDTO;
  }

  @Override
  public CargaInformacionDTO getCargaInformacion() {
    CargaInformacionDTO cargaInformacionDTO = new CargaInformacionDTO();
    // CifrasControlRepositoryOld cifrasControlDAO = new
    // CifrasControlRepositoryOld();
    cargaInformacionDTO = cifrasControlRepository.getCargaInformacion();
    if (cargaInformacionDTO.getResultDT().getId() == 1) {
      Date date = new Date();
      SimpleDateFormat sda = new SimpleDateFormat("yyyy");
      SimpleDateFormat sdm = new SimpleDateFormat("MM");
      SimpleDateFormat sdd = new SimpleDateFormat("dd");
      cargaInformacionDTO.setAnio(sda.format(date));
      cargaInformacionDTO.setMes(sdm.format(date));
      cargaInformacionDTO.setDia(sdd.format(date));
    }
    return cargaInformacionDTO;
  }

  @Override
  public ReporteAvanceDTO getReporteOperaciones() {
    ReporteAvanceDTO avanceDTO = new ReporteAvanceDTO();
    // CifrasControlRepositoryOld cifrasControlDAO = new
    // CifrasControlRepositoryOld();
    avanceDTO = cifrasControlRepository.getReporteOperaciones();
    if (avanceDTO.getResultDT().getId() == 1) {
      Date date = new Date();
      SimpleDateFormat sda = new SimpleDateFormat("yyyy");
      SimpleDateFormat sdm = new SimpleDateFormat("MM");
      SimpleDateFormat sdd = new SimpleDateFormat("dd");
      avanceDTO.setAnio(sda.format(date));
      avanceDTO.setMes(sdm.format(date));
      avanceDTO.setDia(sdd.format(date));
    }
    return avanceDTO;
  }

  @Override
  public ResultDTO saveReportePlaneacion(PlaneacionBodyDTO planeacionBodyDTO) {
    ResultDTO resultDT = new ResultDTO();
    // CifrasControlRepositoryOld cifrasControlDAO = new
    // CifrasControlRepositoryOld();
    resultDT = cifrasControlRepository.savePlaneacion(planeacionBodyDTO);
    return resultDT;
  }

  @Override
  public File generateReports(DescargaInformacionDTO descargaInformacionDTO) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
    List<String> reportDate = new ArrayList<String>();
    // CifrasControlRepositoryOld cifrasControlDAO = new
    // CifrasControlRepositoryOld();
    File generateReports = null;
    PrintWriter writer = null;
    reportDate = cifrasControlRepository.generateDataReport(descargaInformacionDTO);
    if (reportDate.size() > 0) {
      String filepath = "";
      if (descargaInformacionDTO.getResultDT().getId() == 1) {
        filepath = "Reporte Folios Centro " + descargaInformacionDTO.getResultDT().getMsg() + " - "
            + sdf.format(new Date());
      } else if (descargaInformacionDTO.getResultDT().getId() == 2) {
        filepath = "Reporte Entregas Entrantes Centro " + descargaInformacionDTO.getResultDT().getMsg() + " - "
            + sdf.format(new Date());
      } else if (descargaInformacionDTO.getResultDT().getId() == 3) {
        filepath = "Reporte Entregas Salientes Centro " + descargaInformacionDTO.getResultDT().getMsg() + " - "
            + sdf.format(new Date());
      } else if (descargaInformacionDTO.getResultDT().getId() == 4) {
        filepath = "Reporte Ordenes Produccion Centro " + descargaInformacionDTO.getResultDT().getMsg() + " - "
            + sdf.format(new Date());
      } else if (descargaInformacionDTO.getResultDT().getId() == 5) {
        filepath = "Reporte Unidades de Manipulacion Centro " + descargaInformacionDTO.getResultDT().getMsg()
            + " - " + sdf.format(new Date());
      }
      generateReports = new File(filepath + ".csv");
      try {
        writer = new PrintWriter(new OutputStreamWriter(
            new BufferedOutputStream(new FileOutputStream(generateReports)), "UTF-8"));

        writer.append(filepath).println();
        writer.append(descargaInformacionDTO.getResultDT().getId() == 1 ? "Folios"
            : descargaInformacionDTO.getResultDT().getId() == 2 ? "Entregas Entrantes"
                : descargaInformacionDTO.getResultDT().getId() == 3 ? "Entregas Salientes"
                    : descargaInformacionDTO.getResultDT().getId() == 3 ? "Ordenes de Produccion"
                        : descargaInformacionDTO.getResultDT().getId() == 5
                            ? "Unidades de Manipulacion"
                            : "UnexpectedRecord")
            .println();

        Iterator<String> it = reportDate.iterator();
        while (it.hasNext()) {
          writer.append(it.next()).println();
        }

        writer.flush();
        writer.close();

      } catch (UnsupportedEncodingException e) {
        log.error("CifrasControlBO - generateReports - UnsupportedEncodingException: " + e.getMessage());
        generateReports = null;
      } catch (FileNotFoundException e) {
        log.error("CifrasControlBO - generateReports - FileNotFoundException: " + e.getMessage());
        generateReports = null;
      }
    }
    return generateReports;
  }

  @Override
  public File generateReports(CargaInformacionDTO cargaInformacionDTO) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
    CargaInformacionDTO cargaReport = new CargaInformacionDTO();

    // CifrasControlRepositoryOld cifrasControlDAO = new
    // CifrasControlRepositoryOld();
    File generateReports = null;
    PrintWriter writer = null;
    cargaReport = cifrasControlRepository.getCargaInformacion();

    if (cargaReport.getItemDto().size() > 0) {
      String filepath = "Reporte Carga Informacion BCPS - " + sdf.format(new Date());
      generateReports = new File(filepath + ".csv");
      try {
        writer = new PrintWriter(new OutputStreamWriter(
            new BufferedOutputStream(new FileOutputStream(generateReports)), "UTF-8"));

        writer.append(filepath).println();
        writer.append(new CargaInformacionBodyDTO().headerReport()).println();
        Iterator<CargaInformacionBodyDTO> it = cargaReport.getItemDto().iterator();
        while (it.hasNext()) {
          writer.append(it.next().toReport()).println();
        }
        writer.flush();
        writer.close();

      } catch (UnsupportedEncodingException e) {
        log.error("CifrasControlBO - generateReports - UnsupportedEncodingException: " + e.getMessage());
        generateReports = null;
      } catch (FileNotFoundException e) {
        log.error("CifrasControlBO - generateReports - FileNotFoundException: " + e.getMessage());
        generateReports = null;
      }
    }
    return generateReports;
  }

  @Override
  public File generateReports(ReporteAvanceDTO reporteAvanceDTO) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
    ReporteAvanceDTO cargaReport = new ReporteAvanceDTO();

    // CifrasControlRepositoryOld cifrasControlDAO = new
    // CifrasControlRepositoryOld();
    File generateReports = null;
    PrintWriter writer = null;
    cargaReport = cifrasControlRepository.getReporteOperaciones();

    if (cargaReport.getItemDto().size() > 0) {
      String filepath = "Reporte Carga Informacion BCPS - " + sdf.format(new Date());
      generateReports = new File(filepath + ".csv");
      try {
        writer = new PrintWriter(new OutputStreamWriter(
            new BufferedOutputStream(new FileOutputStream(generateReports)), "UTF-8"));

        writer.append(filepath).println();
        writer.append(new ReporteAvanceBodyDTO().toReportHeader()).println();
        Iterator<ReporteAvanceBodyDTO> it = cargaReport.getItemDto().iterator();
        while (it.hasNext()) {
          writer.append(it.next().toReport()).println();
        }
        writer.flush();
        writer.close();

      } catch (UnsupportedEncodingException e) {
        log.error("CifrasControlBO - generateReports - UnsupportedEncodingException: " + e.getMessage());
        generateReports = null;
      } catch (FileNotFoundException e) {
        log.error("CifrasControlBO - generateReports - FileNotFoundException: " + e.getMessage());
        generateReports = null;
      }
    }
    return generateReports;
  }

}
