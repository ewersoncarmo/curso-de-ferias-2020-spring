package com.matera.cursoferias.digitalbank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matera.cursoferias.digitalbank.business.ContaBusiness;
import com.matera.cursoferias.digitalbank.domain.enumerator.Natureza;
import com.matera.cursoferias.digitalbank.domain.enumerator.TipoLancamento;
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.TransferenicaRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ComprovanteResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ExtratoResponseDTO;

@Service
public class ContaService {

	@Autowired
	private ContaBusiness contaBusiness;
	
	public ComprovanteResponseDTO efetuarLancamento(Long id, LancamentoRequestDTO lancamentoRequestDTO, Natureza natureza, TipoLancamento tipoLancamento) {
		return contaBusiness.efetuarLancamento(id, lancamentoRequestDTO, natureza, tipoLancamento);
	}

	public ComprovanteResponseDTO efetuarTransferencia(Long id, TransferenicaRequestDTO transferenciaRequestDTO) {
		return contaBusiness.efetuarTransferencia(id, transferenciaRequestDTO);
	}

	public ExtratoResponseDTO consultarExtratoCompleto(Long id) {
		return contaBusiness.consultarExtratoCompleto(id);
	}

}
