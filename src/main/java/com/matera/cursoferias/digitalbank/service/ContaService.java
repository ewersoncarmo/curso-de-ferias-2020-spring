package com.matera.cursoferias.digitalbank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matera.cursoferias.digitalbank.business.ContaBusiness;
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.TransferenicaRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.LancamentoResponseDTO;

@Service
public class ContaService {

	@Autowired
	private ContaBusiness contaBusiness;
	
	public ContaResponseDTO efetuarLancamento(Long id, LancamentoRequestDTO lancamentoRequestDTO) {
		return contaBusiness.efetuarLancamento(id, lancamentoRequestDTO);
	}

	public ContaResponseDTO efetuarTransferencia(Long id, TransferenicaRequestDTO transferenciaRequestDTO) {
		return contaBusiness.efetuarTransferencia(id, transferenciaRequestDTO);
	}

	public List<LancamentoResponseDTO> consultarExtratoCompleto(Long id) {
		return contaBusiness.consultarExtratoCompleto(id);
	}
}
