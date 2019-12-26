package com.matera.cursoferias.digitalbank.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.matera.cursoferias.digitalbank.business.ContaBusiness;
import com.matera.cursoferias.digitalbank.domain.enumerator.TipoLancamento;
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.TransferenciaRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ComprovanteResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ExtratoResponseDTO;

@Service
public class ContaService {

	private final ContaBusiness contaBusiness;

	public ContaService(ContaBusiness contaBusiness) {
        this.contaBusiness = contaBusiness;
    }

    public ComprovanteResponseDTO efetuaLancamento(Long id, LancamentoRequestDTO lancamentoRequestDTO, TipoLancamento tipoLancamento) {
		return contaBusiness.efetuaLancamento(id, lancamentoRequestDTO, tipoLancamento);
	}

	public ComprovanteResponseDTO efetuaTransferencia(Long id, TransferenciaRequestDTO transferenciaRequestDTO) {
		return contaBusiness.efetuaTransferencia(id, transferenciaRequestDTO);
	}

	public ExtratoResponseDTO consultaExtratoCompleto(Long id) {
		return contaBusiness.consultaExtratoCompleto(id);
	}

	public ExtratoResponseDTO consultaExtratoPorPeriodo(Long id, LocalDate dataInicial, LocalDate dataFinal) {
		return contaBusiness.consultaExtratoPorPeriodo(id, dataInicial, dataFinal);
	}

	public ComprovanteResponseDTO estornaLancamento(Long idConta, Long idLancamento) {
		return contaBusiness.estornaLancamento(idConta, idLancamento);
	}

	public ComprovanteResponseDTO consultaComprovanteLancamento(Long idConta, Long idLancamento) {
		return contaBusiness.consultaComprovanteLancamento(idConta, idLancamento);
	}

	public void removeLancamentoEstorno(Long idConta, Long idLancamento) {
	    contaBusiness.removeLancamentoEstorno(idConta, idLancamento);
    }

    public List<ContaResponseDTO> consultaTodas() {
        return contaBusiness.consultaTodas();
    }

    public void bloqueiaConta(Long id) {
        contaBusiness.bloqueiaConta(id);
    }

    public void desbloqueiaConta(Long id) {
        contaBusiness.desbloqueiaConta(id);
    }

}
