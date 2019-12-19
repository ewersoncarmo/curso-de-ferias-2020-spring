package com.matera.cursoferias.digitalbank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matera.cursoferias.digitalbank.business.ContaBusiness;
import com.matera.cursoferias.digitalbank.domain.enumerator.Natureza;
import com.matera.cursoferias.digitalbank.domain.enumerator.TipoLancamento;
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.TransferenciaRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ComprovanteResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ExtratoResponseDTO;

@Service
public class ContaService {

	@Autowired
	private ContaBusiness contaBusiness;

	public ComprovanteResponseDTO efetuarLancamento(Long id, LancamentoRequestDTO lancamentoRequestDTO, Natureza natureza, TipoLancamento tipoLancamento) {
		return contaBusiness.efetuaLancamento(id, lancamentoRequestDTO, natureza, tipoLancamento);
	}

	public ComprovanteResponseDTO efetuarTransferencia(Long id, TransferenciaRequestDTO transferenciaRequestDTO) {
		return contaBusiness.efetuaTransferencia(id, transferenciaRequestDTO);
	}

	public ExtratoResponseDTO consultarExtratoCompleto(Long id) {
		return contaBusiness.consultaExtratoCompleto(id);
	}

    public List<ContaResponseDTO> consultarTodas() {
        return contaBusiness.consultaTodas();
    }

    public ComprovanteResponseDTO estornarLancamento(Long idConta, Long idLancamento) {
		return contaBusiness.estornaLancamento(idConta, idLancamento);
	}

    public void bloqueiaConta(Long id) {
        contaBusiness.bloqueiaConta(id);
    }

    public void desbloqueiaConta(Long id) {
        contaBusiness.desbloqueiaConta(id);
    }

}
