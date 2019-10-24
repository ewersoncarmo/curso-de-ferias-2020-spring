package com.matera.cursoferias.digitalbank.business;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.matera.cursoferias.digitalbank.domain.entity.Conta;
import com.matera.cursoferias.digitalbank.domain.entity.Lancamento;
import com.matera.cursoferias.digitalbank.domain.entity.Transferencia;
import com.matera.cursoferias.digitalbank.domain.enumerator.Natureza;
import com.matera.cursoferias.digitalbank.domain.enumerator.TipoLancamento;
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ComprovanteResponseDTO;
import com.matera.cursoferias.digitalbank.repository.LancamentoRepository;
import com.matera.cursoferias.digitalbank.repository.TransferenciaRepository;

@Component
public class LancamentoBusiness {

	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private TransferenciaRepository transferenciaRepository;
	
	public Lancamento efetuarLancamento(LancamentoRequestDTO lancamentoRequestDTO, Conta conta, Natureza natureza, TipoLancamento tipoLancamento) {
		Lancamento lancamento = new Lancamento();
		lancamento.setDataHora(LocalDateTime.now());
		lancamento.setCodigoAutenticacao(UUID.randomUUID().toString());
		lancamento.setValor(lancamentoRequestDTO.getValor());
		lancamento.setNatureza(natureza.getCodigo());
		lancamento.setTipoLancamento(tipoLancamento.getCodigo());
		lancamento.setDescricao(lancamentoRequestDTO.getDescricao());
		lancamento.setConta(conta);
		
		return lancamentoRepository.save(lancamento);
	}
	
	public ComprovanteResponseDTO efetuarTransferencia(Lancamento lancamentoDebito, Lancamento lancamentoCredito) {
		Transferencia transferencia = new Transferencia();
		transferencia.setLancamentoDebito(lancamentoDebito);
		transferencia.setLancamentoCredito(lancamentoCredito);
		
		transferenciaRepository.save(transferencia);
		
		return lancamentoEntidadeParaComprovanteResponseDTO(lancamentoDebito);
	}

	public List<ComprovanteResponseDTO> consultarExtratoCompleto(Conta conta) {
		List<Lancamento> lancamentos = lancamentoRepository.findByConta_Id(conta.getId());
		
		List<ComprovanteResponseDTO> comprovantesResponseDTO = new ArrayList<>();
		lancamentos.forEach(l -> comprovantesResponseDTO.add(lancamentoEntidadeParaComprovanteResponseDTO(l)));
		
		return comprovantesResponseDTO;
	}
	
	public ComprovanteResponseDTO lancamentoEntidadeParaComprovanteResponseDTO(Lancamento lancamento) {
		ComprovanteResponseDTO comprovanteResponseDTO = new ComprovanteResponseDTO();
		comprovanteResponseDTO.setIdLancamento(lancamento.getId());
		comprovanteResponseDTO.setCodigoAutenticacao(lancamento.getCodigoAutenticacao());
		comprovanteResponseDTO.setDataHora(lancamento.getDataHora());
		comprovanteResponseDTO.setValor(lancamento.getValor());
		comprovanteResponseDTO.setNatureza(lancamento.getNatureza());
		comprovanteResponseDTO.setTipoLancamento(lancamento.getTipoLancamento());
		comprovanteResponseDTO.setDescricao(lancamento.getDescricao());
		
		return comprovanteResponseDTO;
	}

}
