package com.bmore.prueba.web.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class CreacionEntregaItemDTO {

		private List<CrecionEntregaDTO> item;

		public List<CrecionEntregaDTO> getItem() {
			return item;
		}

		public void setItem(List<CrecionEntregaDTO> item) {
			this.item = item;
		}
}
