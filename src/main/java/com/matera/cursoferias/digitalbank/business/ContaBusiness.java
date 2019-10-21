package com.matera.cursoferias.digitalbank.business;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.matera.cursoferias.digitalbank.domain.entity.Cliente;
import com.matera.cursoferias.digitalbank.domain.entity.Conta;
import com.matera.cursoferias.digitalbank.domain.entity.Lancamento;
import com.matera.cursoferias.digitalbank.domain.entity.Transferencia;
import com.matera.cursoferias.digitalbank.domain.enumerator.Natureza;
import com.matera.cursoferias.digitalbank.domain.enumerator.TipoLancamento;
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.TransferenicaRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.LancamentoResponseDTO;
import com.matera.cursoferias.digitalbank.repository.ContaRepository;
import com.matera.cursoferias.digitalbank.repository.LancamentoRepository;
import com.matera.cursoferias.digitalbank.repository.TransferenciaRepository;
import com.matera.cursoferias.digitalbank.util.exceptions.BusinessException;

@Component
public class ContaBusiness {

	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private TransferenciaRepository transferenciaRepository;
	
	public ContaResponseDTO criarConta(Cliente cliente) {
		validar(cliente);
		
		Conta conta = new Conta();
		conta.setNumeroConta(cliente.getTelefone());
		conta.setSaldo(BigDecimal.ZERO);
		conta.setCliente(cliente);
		
		conta = contaRepository.save(conta);

		return contaEntidadeParaContaResponseDTO(conta);
	}

	@Transactional
	public ContaResponseDTO efetuarLancamento(Long id, LancamentoRequestDTO lancamentoRequestDTO) {
		Conta conta = findById(id);
		conta.setSaldo(calcularSaldo(lancamentoRequestDTO.getNatureza(), lancamentoRequestDTO.getValor(), conta.getSaldo()));
		
		Lancamento lancamento = new Lancamento();
		lancamento.setData(LocalDate.now());
		lancamento.setValor(lancamentoRequestDTO.getValor());
		lancamento.setNatureza(lancamentoRequestDTO.getNatureza().getCodigo());
		lancamento.setTipoLancamento(lancamentoRequestDTO.getTipoLancamento().getCodigo());
		lancamento.setDescricao(lancamentoRequestDTO.getDescricao());
		lancamento.setConta(conta);
		
		lancamentoRepository.save(lancamento);

		contaRepository.save(conta);
		
		return contaEntidadeParaContaResponseDTO(conta);
	}

	@Transactional
	public ContaResponseDTO efetuarTransferencia(Long id, TransferenicaRequestDTO transferenciaRequestDTO) {
		Conta contaDebito = findById(id);

		Conta contaCredito = contaRepository.findByNumeroConta(transferenciaRequestDTO.getNumeroConta());
		if (contaCredito == null) {
			throw new BusinessException(String.format("Conta de destino não encontrada"));
		}
		
		contaDebito.setSaldo(calcularSaldo(Natureza.DEBITO, transferenciaRequestDTO.getValor(), contaDebito.getSaldo()));
		contaCredito.setSaldo(calcularSaldo(Natureza.CREDITO, transferenciaRequestDTO.getValor(), contaCredito.getSaldo()));

		contaRepository.saveAll(Arrays.asList(contaDebito, contaCredito));
		
		Lancamento lancamentoDebito = new Lancamento();
		lancamentoDebito.setData(LocalDate.now());
		lancamentoDebito.setValor(transferenciaRequestDTO.getValor());
		lancamentoDebito.setNatureza(Natureza.DEBITO.getCodigo());
		lancamentoDebito.setTipoLancamento(TipoLancamento.TRANSFERENCIA.getCodigo());
		lancamentoDebito.setDescricao(transferenciaRequestDTO.getDescricao());
		lancamentoDebito.setConta(contaDebito);

		Lancamento lancamentoCredito = new Lancamento();
		lancamentoCredito.setData(LocalDate.now());
		lancamentoCredito.setValor(transferenciaRequestDTO.getValor());
		lancamentoCredito.setNatureza(Natureza.CREDITO.getCodigo());
		lancamentoCredito.setTipoLancamento(TipoLancamento.TRANSFERENCIA.getCodigo());
		lancamentoCredito.setDescricao(transferenciaRequestDTO.getDescricao());
		lancamentoCredito.setConta(contaCredito);
		
		lancamentoRepository.saveAll(Arrays.asList(lancamentoDebito, lancamentoCredito));
		
		Transferencia transferencia = new Transferencia();
		transferencia.setLancamentoDebito(lancamentoDebito);
		transferencia.setLancamentoCredito(lancamentoCredito);
		
		transferenciaRepository.save(transferencia);
		
		return contaEntidadeParaContaResponseDTO(contaDebito);
	}

	public List<LancamentoResponseDTO> consultarextratocompleto(Long id) {
		findById(id);
		List<Lancamento> lancamentos = lancamentoRepository.findByConta_Id(id);
		
		List<LancamentoResponseDTO> lancamentoResponseDTO = new ArrayList<>();
		lancamentos.forEach(l -> lancamentoResponseDTO.add(lancamentoEntidadeParaLancamentoResponseDTO(l)));
		
		return lancamentoResponseDTO;
	}
	
	private Conta findById(Long id) {
		return contaRepository.findById(id).orElseThrow(() -> new BusinessException(String.format("Conta %d não encontrada", id)));
	}
	
	private void validar(Cliente cliente) {
		if (contaRepository.findByNumeroConta(cliente.getTelefone()) != null) {
			throw new BusinessException("Já existe uma Conta cadastrada com o número informado.");
		}
	}
	
	private BigDecimal calcularSaldo(Natureza natureza, BigDecimal valor, BigDecimal saldoAtual) {
		BigDecimal saldoFinal;
		if (natureza == Natureza.DEBITO) {
			saldoFinal = saldoAtual.subtract(valor);

			if (saldoFinal.compareTo(BigDecimal.ZERO) == -1) {
				throw new BusinessException("Saldo indisponível para efetuar lançamento");
			}
		} else {
			saldoFinal = saldoAtual.add(valor);
		}
		
		return saldoFinal;
	}
	
	private ContaResponseDTO contaEntidadeParaContaResponseDTO(Conta conta) {
		ContaResponseDTO contaResponseDTO = new ContaResponseDTO();
		contaResponseDTO.setId(conta.getId());
		contaResponseDTO.setNumeroConta(conta.getNumeroConta());
		contaResponseDTO.setSaldo(conta.getSaldo());
		
		return contaResponseDTO;
	}

	private LancamentoResponseDTO lancamentoEntidadeParaLancamentoResponseDTO(Lancamento lancamento) {
		LancamentoResponseDTO lancamentoResponseDTO = new LancamentoResponseDTO();
		lancamentoResponseDTO.setData(lancamento.getData());
		lancamentoResponseDTO.setValor(lancamento.getValor());
		lancamentoResponseDTO.setNatureza(lancamento.getNatureza());
		lancamentoResponseDTO.setTipoLancamento(lancamento.getTipoLancamento());
		lancamentoResponseDTO.setDescricao(lancamento.getDescricao());
		
		return lancamentoResponseDTO;
	}
}
