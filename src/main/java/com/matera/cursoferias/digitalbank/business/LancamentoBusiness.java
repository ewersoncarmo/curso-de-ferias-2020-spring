package com.matera.cursoferias.digitalbank.business;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.matera.cursoferias.digitalbank.domain.entity.Conta;
import com.matera.cursoferias.digitalbank.domain.entity.Estorno;
import com.matera.cursoferias.digitalbank.domain.entity.Lancamento;
import com.matera.cursoferias.digitalbank.domain.entity.Transferencia;
import com.matera.cursoferias.digitalbank.domain.enumerator.Natureza;
import com.matera.cursoferias.digitalbank.domain.enumerator.SituacaoConta;
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

    private static final String COMPLEMENTO_LANCAMENTO_ESTORNADO = " - Estornado";

    private final LancamentoRepository lancamentoRepository;
	private final TransferenciaRepository transferenciaRepository;
	private final EstornoRepository estornoRepository;

	public LancamentoBusiness(LancamentoRepository lancamentoRepository, TransferenciaRepository transferenciaRepository, EstornoRepository estornoRepository) {
        this.lancamentoRepository = lancamentoRepository;
        this.transferenciaRepository = transferenciaRepository;
        this.estornoRepository = estornoRepository;
    }

    public Lancamento efetuaLancamento(LancamentoRequestDTO lancamentoRequestDTO, Conta conta, Natureza natureza, TipoLancamento tipoLancamento) {
		Lancamento lancamento = Lancamento.builder().dataHora(LocalDateTime.now())
                                            		.codigoAutenticacao(geraAutenticacao())
                                            		.valor(lancamentoRequestDTO.getValor())
                                            		.natureza(natureza.getCodigo())
                                            		.tipoLancamento(tipoLancamento.getCodigo())
                                            		.descricao(lancamentoRequestDTO.getDescricao())
                                            		.conta(conta)
                                            		.build();

		validaLancamento(lancamento);

		return lancamentoRepository.save(lancamento);
	}

    public ComprovanteResponseDTO efetuaTransferencia(Lancamento lancamentoDebito, Lancamento lancamentoCredito) {
		Transferencia transferencia = new Transferencia();

		transferencia.setLancamentoDebito(lancamentoDebito);
		transferencia.setLancamentoCredito(lancamentoCredito);

		transferenciaRepository.save(transferencia);

		return entidadeParaComprovanteResponseDTO(lancamentoDebito);
	}

	public List<ComprovanteResponseDTO> consultaExtratoCompleto(Conta conta) {
		List<Lancamento> lancamentos = lancamentoRepository.findByConta_IdOrderByIdDesc(conta.getId());

		List<ComprovanteResponseDTO> comprovantesResponseDTO = new ArrayList<>();
		lancamentos.forEach(l -> comprovantesResponseDTO.add(entidadeParaComprovanteResponseDTO(l)));

		return comprovantesResponseDTO;
	}

	public List<ComprovanteResponseDTO> consultaExtratoPorPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
		List<Lancamento> lancamentos = lancamentoRepository.consultaLancamentosPorPeriodo(dataInicial, dataFinal);

		List<ComprovanteResponseDTO> comprovantesResponseDTO = new ArrayList<>();
		lancamentos.forEach(l -> comprovantesResponseDTO.add(entidadeParaComprovanteResponseDTO(l)));

		return comprovantesResponseDTO;
	}

	public ComprovanteResponseDTO entidadeParaComprovanteResponseDTO(Lancamento lancamento) {
		return ComprovanteResponseDTO.builder().idLancamento(lancamento.getId())
                                        	   .codigoAutenticacao(lancamento.getCodigoAutenticacao())
                                        	   .dataHora(lancamento.getDataHora())
                                        	   .valor(lancamento.getValor())
                                        	   .natureza(lancamento.getNatureza())
                                        	   .tipoLancamento(lancamento.getTipoLancamento())
                                        	   .descricao(lancamento.getDescricao())
                                        	   .build();
	}

	public ComprovanteResponseDTO estornaLancamento(Long idConta, Long idLancamento) {
		Lancamento lancamento = lancamentoRepository.findByIdAndConta_Id(idLancamento, idConta).orElse(null);
		Transferencia transferencia = transferenciaRepository.consultaTransferenciaPorIdLancamento(idLancamento).orElse(null);

		validaEstorno(lancamento, transferencia, idConta, idLancamento);

		if (transferencia != null) {
			return trataEstornoTransferencia(transferencia);
		} else {
			return trataEstornoLancamento(lancamento);
		}
	}

	public ComprovanteResponseDTO consultaComprovanteLancamento(Long idConta, Long idLancamento) {
		Lancamento lancamento = buscaLancamentoConta(idConta, idLancamento);

		return entidadeParaComprovanteResponseDTO(lancamento);
	}

	public void removeLancamentoEstorno(Long idConta, Long idLancamento) {
	    buscaLancamentoConta(idConta, idLancamento);
	    Estorno estorno = estornoRepository.findByLancamentoEstorno_Id(idLancamento).orElseThrow(() -> new BusinessException("DB-16"));
	    Lancamento lancamentoOriginal = estorno.getLancamentoOriginal();
	    Natureza natureza = inverteNatureza(estorno.getLancamentoEstorno());

	    lancamentoOriginal.getConta().setSaldo(DigitalBankUtils.calculaSaldo(natureza, lancamentoOriginal.getValor(), lancamentoOriginal.getConta().getSaldo()));
	    lancamentoOriginal.setDescricao(lancamentoOriginal.getDescricao().replace(COMPLEMENTO_LANCAMENTO_ESTORNADO, ""));

	    lancamentoRepository.save(lancamentoOriginal);
	    estornoRepository.delete(estorno);
	    lancamentoRepository.delete(estorno.getLancamentoEstorno());
    }

	private void validaLancamento(Lancamento lancamento) {
	    if (SituacaoConta.BLOQUEADA.getCodigo().equals(lancamento.getConta().getSituacao())) {
            throw new BusinessException("DB-15", lancamento.getConta().getId());
        }

	    if (Natureza.DEBITO.getCodigo().equals(lancamento.getNatureza()) && lancamento.getConta().getSaldo().compareTo(lancamento.getValor()) < 0) {
            throw new BusinessException("DB-6");
        }
    }

	private void validaEstorno(Lancamento lancamento, Transferencia transferencia, Long idConta, Long idLancamento) {
		if (lancamento == null) {
			throw new BusinessException("DB-7", idLancamento, idConta);
		}

		if (TipoLancamento.ESTORNO.getCodigo().equals(lancamento.getTipoLancamento())) {
			throw new BusinessException("DB-8", lancamento.getTipoLancamento());
		}

		if (estornoRepository.findByLancamentoOriginal_Id(lancamento.getId()).isPresent()) {
			throw new BusinessException("DB-9");
		}

		if (TipoLancamento.TRANSFERENCIA.getCodigo().equals(lancamento.getTipoLancamento()) && !lancamento.getId().equals(transferencia.getLancamentoCredito().getId())) {
			throw new BusinessException("DB-10");
		}

		if (SituacaoConta.BLOQUEADA.getCodigo().equals(lancamento.getConta().getSituacao())) {
            throw new BusinessException("DB-15", lancamento.getConta().getId());
        }

		if (Natureza.CREDITO.getCodigo().equals(lancamento.getNatureza()) && lancamento.getConta().getSaldo().compareTo(lancamento.getValor()) < 0) {
            throw new BusinessException("DB-11");
        }
	}

	private ComprovanteResponseDTO trataEstornoTransferencia(Transferencia transferencia) {
		trataEstornoLancamento(transferencia.getLancamentoDebito());
		return trataEstornoLancamento(transferencia.getLancamentoCredito());
	}

	private ComprovanteResponseDTO trataEstornoLancamento(Lancamento lancamento) {
		Conta conta = lancamento.getConta();
		Natureza natureza = inverteNatureza(lancamento);
		conta.setSaldo(DigitalBankUtils.calculaSaldo(natureza, lancamento.getValor(), conta.getSaldo()));

		Lancamento lancamentoEstorno = Lancamento.builder().codigoAutenticacao(geraAutenticacao())
														   .conta(conta)
														   .dataHora(LocalDateTime.now())
														   .descricao("Estorno do lançamento " + lancamento.getId())
														   .natureza(natureza.getCodigo())
														   .tipoLancamento(TipoLancamento.ESTORNO.getCodigo())
														   .valor(lancamento.getValor())
														   .build();

		lancamento.setDescricao(lancamento.getDescricao() + COMPLEMENTO_LANCAMENTO_ESTORNADO);
		lancamentoRepository.save(lancamento);
		lancamentoRepository.save(lancamentoEstorno);

		Estorno estorno = Estorno.builder().lancamentoEstorno(lancamentoEstorno)
										   .lancamentoOriginal(lancamento)
										   .build();

		estornoRepository.save(estorno);

		return entidadeParaComprovanteResponseDTO(lancamentoEstorno);
	}

	private Lancamento buscaLancamentoConta(Long idConta, Long idLancamento) {
        return lancamentoRepository.findByIdAndConta_Id(idLancamento, idConta).orElseThrow(() -> new BusinessException("DB-7", idLancamento, idConta));
    }

	private String geraAutenticacao() {
		return UUID.randomUUID().toString();
	}

	private Natureza inverteNatureza(Lancamento lancamento) {
		return Natureza.DEBITO.getCodigo().equals(lancamento.getNatureza()) ? Natureza.CREDITO : Natureza.DEBITO;
	}

}
