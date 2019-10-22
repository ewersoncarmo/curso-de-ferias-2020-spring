package com.matera.cursoferias.digitalbank.business;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.matera.cursoferias.digitalbank.domain.entity.Cliente;
import com.matera.cursoferias.digitalbank.domain.entity.Conta;
import com.matera.cursoferias.digitalbank.domain.enumerator.Natureza;
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.TransferenicaRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.LancamentoResponseDTO;
import com.matera.cursoferias.digitalbank.repository.ContaRepository;
import com.matera.cursoferias.digitalbank.util.exceptions.BusinessException;

@Component
public class ContaBusiness {

	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private LancamentoBusiness lancamentoBusiness;
	
	public ContaResponseDTO cadastrar(Cliente cliente) {
		validar(cliente);
		
		Conta conta = new Conta();
		conta.setNumeroConta(cliente.getTelefone());
		conta.setSaldo(BigDecimal.ZERO);
		conta.setCliente(cliente);
		
		conta = contaRepository.save(conta);

		return entidadeParaResponseDTO(conta);
	}

	@Transactional
	public ContaResponseDTO efetuarLancamento(Long id, LancamentoRequestDTO lancamentoRequestDTO) {
		Conta conta = findById(id);
		conta.setSaldo(calcularSaldo(lancamentoRequestDTO.getNatureza(), lancamentoRequestDTO.getValor(), conta.getSaldo()));
		
		lancamentoBusiness.efetuarLancamento(lancamentoRequestDTO, conta);
		
		conta = contaRepository.save(conta);
		
		return entidadeParaResponseDTO(conta);
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
		
		lancamentoBusiness.efetuarTransferencia(contaDebito, contaCredito, transferenciaRequestDTO);
		
		return entidadeParaResponseDTO(contaDebito);
	}

	public List<LancamentoResponseDTO> consultarExtratoCompleto(Long id) {
		return lancamentoBusiness.consultarExtratoCompleto(findById(id));
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
	
	private ContaResponseDTO entidadeParaResponseDTO(Conta conta) {
		ContaResponseDTO contaResponseDTO = new ContaResponseDTO();
		contaResponseDTO.setId(conta.getId());
		contaResponseDTO.setNumeroConta(conta.getNumeroConta());
		contaResponseDTO.setSaldo(conta.getSaldo());
		
		return contaResponseDTO;
	}
	
}
