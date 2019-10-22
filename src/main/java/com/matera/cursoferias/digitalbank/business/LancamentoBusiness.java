package com.matera.cursoferias.digitalbank.business;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.matera.cursoferias.digitalbank.domain.entity.Conta;
import com.matera.cursoferias.digitalbank.domain.entity.Lancamento;
import com.matera.cursoferias.digitalbank.domain.entity.Transferencia;
import com.matera.cursoferias.digitalbank.domain.enumerator.Natureza;
import com.matera.cursoferias.digitalbank.domain.enumerator.TipoLancamento;
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.TransferenicaRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.LancamentoResponseDTO;
import com.matera.cursoferias.digitalbank.repository.LancamentoRepository;
import com.matera.cursoferias.digitalbank.repository.TransferenciaRepository;

@Component
public class LancamentoBusiness {

	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private TransferenciaRepository transferenciaRepository;
	
	public Lancamento efetuarLancamento(LancamentoRequestDTO lancamentoRequestDTO, Conta conta) {
		Lancamento lancamento = new Lancamento();
		lancamento.setData(LocalDate.now());
		lancamento.setValor(lancamentoRequestDTO.getValor());
		lancamento.setNatureza(lancamentoRequestDTO.getNatureza().getCodigo());
		lancamento.setTipoLancamento(lancamentoRequestDTO.getTipoLancamento().getCodigo());
		lancamento.setDescricao(lancamentoRequestDTO.getDescricao());
		lancamento.setConta(conta);
		
		return lancamentoRepository.save(lancamento);
	}

	public void efetuarTransferencia(Conta contaDebito, Conta contaCredito, TransferenicaRequestDTO transferenciaRequestDTO) {
		Lancamento lancamentoDebito = efetuarLancamento(transferenciaRequestDTO, contaDebito, Natureza.DEBITO);
		Lancamento lancamentoCredito = efetuarLancamento(transferenciaRequestDTO, contaCredito, Natureza.CREDITO);
		
		Transferencia transferencia = new Transferencia();
		transferencia.setLancamentoDebito(lancamentoDebito);
		transferencia.setLancamentoCredito(lancamentoCredito);
		
		transferenciaRepository.save(transferencia);
	}

	public List<LancamentoResponseDTO> consultarExtratoCompleto(Conta conta) {
		List<Lancamento> lancamentos = lancamentoRepository.findByConta_Id(conta.getId());
		
		List<LancamentoResponseDTO> lancamentoResponseDTO = new ArrayList<>();
		lancamentos.forEach(l -> lancamentoResponseDTO.add(entidadeParaResponseDTO(l)));
		
		return lancamentoResponseDTO;
	}
	
	private Lancamento efetuarLancamento(TransferenicaRequestDTO transferenciaRequestDTO, Conta conta, Natureza natureza) {
		LancamentoRequestDTO lancamentoRequestDTO = new LancamentoRequestDTO();
		lancamentoRequestDTO.setValor(transferenciaRequestDTO.getValor());
		lancamentoRequestDTO.setNatureza(natureza);
		lancamentoRequestDTO.setTipoLancamento(TipoLancamento.TRANSFERENCIA);
		lancamentoRequestDTO.setDescricao(transferenciaRequestDTO.getDescricao());
		
		return efetuarLancamento(lancamentoRequestDTO, conta);
	}

	private LancamentoResponseDTO entidadeParaResponseDTO(Lancamento lancamento) {
		LancamentoResponseDTO lancamentoResponseDTO = new LancamentoResponseDTO();
		lancamentoResponseDTO.setData(lancamento.getData());
		lancamentoResponseDTO.setValor(lancamento.getValor());
		lancamentoResponseDTO.setNatureza(lancamento.getNatureza());
		lancamentoResponseDTO.setTipoLancamento(lancamento.getTipoLancamento());
		lancamentoResponseDTO.setDescricao(lancamento.getDescricao());
		
		return lancamentoResponseDTO;
	}
}
