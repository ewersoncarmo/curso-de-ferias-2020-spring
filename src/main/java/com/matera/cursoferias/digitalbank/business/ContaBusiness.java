package com.matera.cursoferias.digitalbank.business;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.matera.cursoferias.digitalbank.domain.entity.Cliente;
import com.matera.cursoferias.digitalbank.domain.entity.Conta;
import com.matera.cursoferias.digitalbank.domain.entity.Lancamento;
import com.matera.cursoferias.digitalbank.domain.enumerator.Natureza;
import com.matera.cursoferias.digitalbank.domain.enumerator.TipoLancamento;
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.TransferenicaRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ComprovanteResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ExtratoResponseDTO;
import com.matera.cursoferias.digitalbank.repository.ContaRepository;
import com.matera.cursoferias.digitalbank.util.exceptions.BusinessException;

@Component
public class ContaBusiness {

	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private LancamentoBusiness lancamentoBusiness;
	
	public ContaResponseDTO cadastrar(Cliente cliente) {
		int numeroAgencia = new Random().nextInt(5);

		validar(numeroAgencia, cliente);
		
		Conta conta = new Conta();
		conta.setNumeroAgencia(numeroAgencia);
		conta.setNumeroConta(cliente.getTelefone());
		conta.setSaldo(BigDecimal.ZERO);
		conta.setCliente(cliente);
		
		conta = contaRepository.save(conta);

		return entidadeParaResponseDTO(conta);
	}

	@Transactional
	public ComprovanteResponseDTO efetuarLancamento(Long id, LancamentoRequestDTO lancamentoRequestDTO, Natureza natureza, TipoLancamento tipoLancamento) {
		Conta conta = findById(id);
		
		Lancamento lancamento = criarLancamento(lancamentoRequestDTO, conta, natureza, tipoLancamento);
				
		return lancamentoBusiness.lancamentoEntidadeParaComprovanteResponseDTO(lancamento);
	}

	@Transactional
	public ComprovanteResponseDTO efetuarTransferencia(Long id, TransferenicaRequestDTO transferenciaRequestDTO) {
		Conta contaDebito = findById(id);
		
		Conta contaCredito = contaRepository.findByNumeroAgenciaAndNumeroConta(transferenciaRequestDTO.getNumeroAgencia(), transferenciaRequestDTO.getNumeroConta());
		if (contaCredito == null) {
			throw new BusinessException(String.format("Conta de destino não encontrada"));
		}
		
		Lancamento lancamentoDebito = criarLancamento(new LancamentoRequestDTO(transferenciaRequestDTO.getValor(), transferenciaRequestDTO.getDescricao()), contaDebito, Natureza.DEBITO, TipoLancamento.TRANSFERENCIA);
		Lancamento lancamentoCredito = criarLancamento(new LancamentoRequestDTO(transferenciaRequestDTO.getValor(), transferenciaRequestDTO.getDescricao()), contaCredito, Natureza.CREDITO, TipoLancamento.TRANSFERENCIA);
		
		return lancamentoBusiness.efetuarTransferencia(lancamentoDebito, lancamentoCredito);
	}
	
	public ExtratoResponseDTO consultarExtratoCompleto(Long id) {
		Conta conta = findById(id);
		
		List<ComprovanteResponseDTO> comprovantesResponseDTO = lancamentoBusiness.consultarExtratoCompleto(conta);
		
		ExtratoResponseDTO extratoResponseDTO = new ExtratoResponseDTO();
		extratoResponseDTO.setConta(entidadeParaResponseDTO(conta));
		extratoResponseDTO.setLancamentos(comprovantesResponseDTO);
		
		return extratoResponseDTO;
	}
	
	private Conta findById(Long id) {
		return contaRepository.findById(id).orElseThrow(() -> new BusinessException(String.format("Conta %d não encontrada", id)));
	}
	
	private void validar(Integer numeroAgencia, Cliente cliente) {
		if (contaRepository.findByNumeroAgenciaAndNumeroConta(numeroAgencia, cliente.getTelefone()) != null) {
			throw new BusinessException("Já existe uma Conta cadastrada com o número informado.");
		}
	}
	
	private Lancamento criarLancamento(LancamentoRequestDTO lancamentoRequestDTO, Conta conta, Natureza natureza, TipoLancamento tipoLancamento) {
		conta.setSaldo(calcularSaldo(natureza, lancamentoRequestDTO.getValor(), conta.getSaldo()));
		
		conta = contaRepository.save(conta);
		
		return lancamentoBusiness.efetuarLancamento(lancamentoRequestDTO, conta, natureza, tipoLancamento);
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
	
	private ContaResponseDTO entidadeParaResponseDTO(Conta conta) {
		ContaResponseDTO contaResponseDTO = new ContaResponseDTO();
		contaResponseDTO.setId(conta.getId());
		contaResponseDTO.setNumeroAgencia(conta.getNumeroAgencia());
		contaResponseDTO.setNumeroConta(conta.getNumeroConta());
		contaResponseDTO.setSaldo(conta.getSaldo());
		
		return contaResponseDTO;
	}

}
