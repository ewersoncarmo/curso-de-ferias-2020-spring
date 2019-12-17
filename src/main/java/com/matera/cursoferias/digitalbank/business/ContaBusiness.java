package com.matera.cursoferias.digitalbank.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.matera.cursoferias.digitalbank.domain.entity.Cliente;
import com.matera.cursoferias.digitalbank.domain.entity.Conta;
import com.matera.cursoferias.digitalbank.domain.entity.Lancamento;
import com.matera.cursoferias.digitalbank.domain.enumerator.Natureza;
import com.matera.cursoferias.digitalbank.domain.enumerator.TipoLancamento;
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.TransferenciaRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ComprovanteResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ExtratoResponseDTO;
import com.matera.cursoferias.digitalbank.exception.BusinessException;
import com.matera.cursoferias.digitalbank.repository.ContaRepository;

@Component
public class ContaBusiness {

	@Autowired
	private ContaRepository contaRepository;

	@Autowired
	private LancamentoBusiness lancamentoBusiness;

	@Value("${agencia.numeroMaximo:5}")
	private Integer numeroMaximoAgencia;

	public ContaResponseDTO cadastrar(Cliente cliente) {
		validar(cliente);

		Integer numeroAgencia = new Random().nextInt(numeroMaximoAgencia) + 1;
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
	public ComprovanteResponseDTO efetuarTransferencia(Long id, TransferenciaRequestDTO transferenciaRequestDTO) {
		Conta contaDebito = findById(id);

		Conta contaCredito = contaRepository.findByNumeroAgenciaAndNumeroConta(transferenciaRequestDTO.getNumeroAgencia(), transferenciaRequestDTO.getNumeroConta());
		if (contaCredito == null) {
			throw new BusinessException("DB-5", transferenciaRequestDTO.getNumeroAgencia(), transferenciaRequestDTO.getNumeroConta());
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

	public List<ContaResponseDTO> consultarTodas() {
	    List<Conta> contas = contaRepository.findAll();
	    List<ContaResponseDTO> contasResponseDTO = new ArrayList<>();

	    contas.forEach(conta -> contasResponseDTO.add(entidadeParaResponseDTO(conta)));

        return contasResponseDTO;
    }

	private Conta findById(Long id) {
		return contaRepository.findById(id).orElseThrow(() -> new BusinessException("DB-3", id));
	}

	private void validar(Cliente cliente) {
		if (contaRepository.findByNumeroConta(cliente.getTelefone()) != null) {
			throw new BusinessException("DB-4", cliente.getTelefone().toString());
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

			if (saldoFinal.compareTo(BigDecimal.ZERO) < 0) {
				throw new BusinessException("DB-6");
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
