package com.matera.cursoferias.digitalbank.business;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.matera.cursoferias.digitalbank.domain.entity.Conta;
import com.matera.cursoferias.digitalbank.domain.entity.Estorno;
import com.matera.cursoferias.digitalbank.domain.entity.Lancamento;
import com.matera.cursoferias.digitalbank.domain.entity.Transferencia;
import com.matera.cursoferias.digitalbank.domain.enumerator.Natureza;
import com.matera.cursoferias.digitalbank.domain.enumerator.TipoLancamento;
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ComprovanteResponseDTO;
import com.matera.cursoferias.digitalbank.exception.BusinessException;
import com.matera.cursoferias.digitalbank.repository.EstornoRepository;
import com.matera.cursoferias.digitalbank.repository.LancamentoRepository;
import com.matera.cursoferias.digitalbank.repository.TransferenciaRepository;
import com.matera.cursoferias.digitalbank.utils.DigitalBankUtils;

@Component
public class LancamentoBusiness {

	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private TransferenciaRepository transferenciaRepository;

	@Autowired
	private EstornoRepository estornoRepository;

	public Lancamento efetuarLancamento(LancamentoRequestDTO lancamentoRequestDTO, Conta conta, Natureza natureza, TipoLancamento tipoLancamento) {
		Lancamento lancamento = new Lancamento();

		lancamento.setDataHora(LocalDateTime.now());
		lancamento.setCodigoAutenticacao(geraAutenticacao());
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

		return entidadeParaComprovanteResponseDTO(lancamentoDebito);
	}

	public List<ComprovanteResponseDTO> consultarExtratoCompleto(Conta conta) {
		List<Lancamento> lancamentos = lancamentoRepository.findByConta_Id(conta.getId());

		List<ComprovanteResponseDTO> comprovantesResponseDTO = new ArrayList<>();
		lancamentos.forEach(l -> comprovantesResponseDTO.add(entidadeParaComprovanteResponseDTO(l)));

		return comprovantesResponseDTO;
	}

	public ComprovanteResponseDTO entidadeParaComprovanteResponseDTO(Lancamento lancamento) {
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

	public ComprovanteResponseDTO estornarLancamento(Long idConta, Long idLancamento) {
		Lancamento lancamento = lancamentoRepository.findByIdAndConta_Id(idLancamento, idConta);
		Transferencia transferencia = transferenciaRepository.buscaTransferenciaPorIdLancamento(idLancamento);

		validaEstorno(lancamento, transferencia, idConta, idLancamento);

		if (TipoLancamento.TRANSFERENCIA.getCodigo().equals(lancamento.getTipoLancamento())) {
			return trataEstornoTransferencia(transferencia);
		} else {
			return trataEstornoLancamento(lancamento);
		}
	}

	private void validaEstorno(Lancamento lancamento, Transferencia transferencia, Long idConta, Long idLancamento) {
		if (lancamento == null) {
			throw new BusinessException("DB-7", idLancamento, idConta);
		}

		if (TipoLancamento.ESTORNO.getCodigo().equals(lancamento.getTipoLancamento())) {
			throw new BusinessException("DB-8", lancamento.getTipoLancamento());
		}

		if (TipoLancamento.TRANSFERENCIA.getCodigo().equals(lancamento.getTipoLancamento())) {
			if (!lancamento.getId().equals(transferencia.getLancamentoCredito().getId())) {
				throw new BusinessException("DB-9");
			}

		    if (transferencia.getLancamentoCredito().getConta().getSaldo().compareTo(lancamento.getValor()) < 0) {
				throw new BusinessException("DB-10");
		    }
		}

		//TODO validar se as contas envolvidas estão ativas para realizar o estorno
	}

	private ComprovanteResponseDTO trataEstornoTransferencia(Transferencia transferencia) {
		trataEstornoLancamento(transferencia.getLancamentoDebito());
		return trataEstornoLancamento(transferencia.getLancamentoCredito());
	}

	private ComprovanteResponseDTO trataEstornoLancamento(Lancamento lancamento) {
		Conta conta = lancamento.getConta();
		Natureza natureza = defineNaturezaEstorno(lancamento);
		conta.setSaldo(DigitalBankUtils.calculaSaldo(natureza, lancamento.getValor(), conta.getSaldo()));

		Lancamento lancamentoEstorno = Lancamento.builder().codigoAutenticacao(geraAutenticacao())
														   .conta(conta)
														   .dataHora(LocalDateTime.now())
														   .descricao("Estorno do lançamento " + lancamento.getId())
														   .natureza(natureza.getCodigo())
														   .tipoLancamento(TipoLancamento.ESTORNO.getCodigo())
														   .valor(lancamento.getValor())
														   .build();

		lancamentoRepository.saveAndFlush(lancamentoEstorno);

		Estorno estorno = Estorno.builder().lancamentoEstorno(lancamentoEstorno)
										   .lancamentoOriginal(lancamento)
										   .build();

		estornoRepository.save(estorno);

		return entidadeParaComprovanteResponseDTO(lancamentoEstorno);
	}

	private String geraAutenticacao() {
		return UUID.randomUUID().toString();
	}

	private Natureza defineNaturezaEstorno(Lancamento lancamento) {
		return Natureza.DEBITO.getCodigo().equals(lancamento.getNatureza()) ? Natureza.CREDITO : Natureza.DEBITO;
	}

}
