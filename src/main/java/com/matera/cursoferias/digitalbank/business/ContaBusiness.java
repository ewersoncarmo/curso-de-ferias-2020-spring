package com.matera.cursoferias.digitalbank.business;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import com.matera.cursoferias.digitalbank.domain.enumerator.SituacaoConta;
import com.matera.cursoferias.digitalbank.domain.enumerator.TipoLancamento;
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.TransferenciaRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ComprovanteResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ExtratoResponseDTO;
import com.matera.cursoferias.digitalbank.exception.BusinessException;
import com.matera.cursoferias.digitalbank.repository.ContaRepository;
import com.matera.cursoferias.digitalbank.utils.DigitalBankUtils;

@Component
public class ContaBusiness {

	@Autowired
	private ContaRepository contaRepository;

	@Autowired
	private LancamentoBusiness lancamentoBusiness;

	@Value("${agencia.numeroMaximo:5}")
	private Integer numeroMaximoAgencia;

	public ContaResponseDTO cadastra(Cliente cliente) {
		validaCadastro(cliente);

		Integer numeroAgencia = new Random().nextInt(numeroMaximoAgencia) + 1;
		Conta conta = Conta.builder().numeroAgencia(numeroAgencia)
		                             .numeroConta(cliente.getTelefone())
		                             .saldo(BigDecimal.ZERO)
		                             .cliente(cliente)
		                             .situacao(SituacaoConta.ABERTA.getCodigo())
		                             .build();

		contaRepository.save(conta);

		return entidadeParaResponseDTO(conta);
	}

	@Transactional
	public ComprovanteResponseDTO efetuaLancamento(Long id, LancamentoRequestDTO lancamentoRequestDTO, Natureza natureza, TipoLancamento tipoLancamento) {
		Conta conta = findById(id);

		Lancamento lancamento = insereLancamento(lancamentoRequestDTO, conta, natureza, tipoLancamento);

		return lancamentoBusiness.entidadeParaComprovanteResponseDTO(lancamento);
	}

	@Transactional
	public ComprovanteResponseDTO efetuaTransferencia(Long id, TransferenciaRequestDTO transferenciaRequestDTO) {
		Conta contaDebito = findById(id);

		Conta contaCredito = contaRepository.findByNumeroAgenciaAndNumeroConta(transferenciaRequestDTO.getNumeroAgencia(), transferenciaRequestDTO.getNumeroConta())
		                                    .orElseThrow(() -> new BusinessException("DB-5", transferenciaRequestDTO.getNumeroAgencia(), transferenciaRequestDTO.getNumeroConta().toString()));

		Lancamento lancamentoDebito = insereLancamento(new LancamentoRequestDTO(transferenciaRequestDTO.getValor(), transferenciaRequestDTO.getDescricao()), contaDebito, Natureza.DEBITO, TipoLancamento.TRANSFERENCIA);
		Lancamento lancamentoCredito = insereLancamento(new LancamentoRequestDTO(transferenciaRequestDTO.getValor(), transferenciaRequestDTO.getDescricao()), contaCredito, Natureza.CREDITO, TipoLancamento.TRANSFERENCIA);

		return lancamentoBusiness.efetuaTransferencia(lancamentoDebito, lancamentoCredito);
	}

	public ExtratoResponseDTO consultaExtratoCompleto(Long id) {
		Conta conta = findById(id);

		List<ComprovanteResponseDTO> comprovantesResponseDTO = lancamentoBusiness.consultaExtratoCompleto(conta);

		ExtratoResponseDTO extratoResponseDTO = new ExtratoResponseDTO();
		extratoResponseDTO.setConta(entidadeParaResponseDTO(conta));
		extratoResponseDTO.setLancamentos(comprovantesResponseDTO);

		return extratoResponseDTO;
	}

	public ExtratoResponseDTO consultaExtratoPorPeriodo(Long id, LocalDate dataInicial, LocalDate dataFinal) {
		Conta conta = findById(id);

		List<ComprovanteResponseDTO> comprovantesResponseDTO = lancamentoBusiness.consultaExtratoPorPeriodo(dataInicial, dataFinal);

		ExtratoResponseDTO extratoResponseDTO = new ExtratoResponseDTO();
		extratoResponseDTO.setConta(entidadeParaResponseDTO(conta));
		extratoResponseDTO.setLancamentos(comprovantesResponseDTO);

		return extratoResponseDTO;
	}

	public ComprovanteResponseDTO estornaLancamento(Long idConta, Long idLancamento) {
		return lancamentoBusiness.estornaLancamento(idConta, idLancamento);
	}

	public List<ContaResponseDTO> consultaTodas() {
	    List<Conta> contas = contaRepository.findAll();
	    List<ContaResponseDTO> contasResponseDTO = new ArrayList<>();

	    contas.forEach(conta -> contasResponseDTO.add(entidadeParaResponseDTO(conta)));

        return contasResponseDTO;
    }

	public ContaResponseDTO consultaContaPorIdCliente(Long idCliente) {
	    Conta conta = contaRepository.findByCliente_Id(idCliente).orElseThrow(() -> new BusinessException("DB-12", idCliente));

	    return entidadeParaResponseDTO(conta);
	}

	public void bloqueiaConta(Long id) {
	    Conta conta = findById(id);

	    validaBloqueio(conta);

	    conta.setSituacao(SituacaoConta.BLOQUEADA.getCodigo());
	    contaRepository.save(conta);
	}

	public void desbloqueiaConta(Long id) {
        Conta conta = findById(id);

        validaDesbloqueio(conta);

        conta.setSituacao(SituacaoConta.ABERTA.getCodigo());
        contaRepository.save(conta);
    }

    private Conta findById(Long id) {
		return contaRepository.findById(id).orElseThrow(() -> new BusinessException("DB-3", id));
	}

	private void validaCadastro(Cliente cliente) {
		if (contaRepository.findByNumeroConta(cliente.getTelefone()).isPresent()) {
			throw new BusinessException("DB-4", cliente.getTelefone().toString());
		}
	}

	private Lancamento insereLancamento(LancamentoRequestDTO lancamentoRequestDTO, Conta conta, Natureza natureza, TipoLancamento tipoLancamento) {
	    Lancamento lancamento = lancamentoBusiness.efetuaLancamento(lancamentoRequestDTO, conta, natureza, tipoLancamento);

		atualizaSaldo(conta, lancamento.getValor(), natureza);

        return lancamento;
	}

    private void atualizaSaldo(Conta conta, BigDecimal valorLancamento, Natureza natureza) {
        BigDecimal saldo = DigitalBankUtils.calculaSaldo(natureza, valorLancamento, conta.getSaldo());

		conta.setSaldo(saldo);
		contaRepository.save(conta);
    }

    private void validaBloqueio(Conta conta) {
        if (SituacaoConta.BLOQUEADA.getCodigo().equals(conta.getSituacao())) {
            throw new BusinessException("DB-13", conta.getId());
        }
    }

    private void validaDesbloqueio(Conta conta) {
        if (SituacaoConta.ABERTA.getCodigo().equals(conta.getSituacao())) {
            throw new BusinessException("DB-14", conta.getId());
        }
    }

	private ContaResponseDTO entidadeParaResponseDTO(Conta conta) {
		return ContaResponseDTO.builder().idCliente(conta.getCliente().getId())
		                                 .idConta(conta.getId())
                                         .numeroAgencia(conta.getNumeroAgencia())
                                         .numeroConta(conta.getNumeroConta())
                                         .situacao(conta.getSituacao())
                                         .saldo(conta.getSaldo())
                                         .build();
	}

}
