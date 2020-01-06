package com.matera.cursoferias.digitalbank.business;

import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildClienteEntidade;
import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildComprovanteResponseDTO;
import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildContaEntidade;
import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildLancamentoEntidade;
import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildLancamentoRequestDTO;
import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildTransferenciaRequestDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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

@ExtendWith(MockitoExtension.class)
public class ContaBusinessTest {

    private static final Integer NUMERO_MAXIMO_AGENCIA = 5;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private LancamentoBusiness lancamentoBusiness;

    @InjectMocks
    private ContaBusiness contaBusiness;

    @BeforeEach
    public void beforeAll() {
        ReflectionTestUtils.setField(contaBusiness, "numeroMaximoAgencia", NUMERO_MAXIMO_AGENCIA);
    }

    @Test
    public void cadastraContaComSucesso() {
        Cliente cliente = buildClienteEntidade();

        when(contaRepository.findByNumeroConta(eq(cliente.getTelefone()))).thenReturn(Optional.empty());

        ContaResponseDTO contaResponse = contaBusiness.cadastra(cliente);

        verify(contaRepository).findByNumeroConta(eq(cliente.getTelefone()));
        verify(contaRepository).save(any(Conta.class));
        verifyNoMoreInteractions(contaRepository);
        verifyNoInteractions(lancamentoBusiness);

        assertNotNull(contaResponse);
    }

    @Test
    public void cadastraContaJaExistente() {
        Cliente cliente = buildClienteEntidade();
        Conta contaMock = buildContaEntidade();

        when(contaRepository.findByNumeroConta(eq(cliente.getTelefone()))).thenReturn(Optional.of(contaMock));

        BusinessException businessException = assertThrows(BusinessException.class, () -> contaBusiness.cadastra(cliente));

        verify(contaRepository).findByNumeroConta(eq(cliente.getTelefone()));
        verifyNoMoreInteractions(contaRepository);
        verifyNoInteractions(lancamentoBusiness);

        assertEquals("DB-4", businessException.getCodigoErro());
    }

    @Test
    public void efetuaLancamentoCreditoComSucesso() {
        Natureza natureza = Natureza.CREDITO;
        TipoLancamento tipoLancamento = TipoLancamento.DEPOSITO;
        BigDecimal valor = BigDecimal.valueOf(100);

        Conta contaMock = buildContaEntidade();
        BigDecimal saldoContaAntes = contaMock.getSaldo();
        LancamentoRequestDTO lancamentoRequest = buildLancamentoRequestDTO(valor);
        Lancamento lancamentoMock = buildLancamentoEntidade(tipoLancamento, natureza, valor);

        when(contaRepository.findById(eq(contaMock.getId()))).thenReturn(Optional.of(contaMock));
        when(lancamentoBusiness.efetuaLancamento(eq(lancamentoRequest), eq(contaMock), eq(natureza), eq(tipoLancamento))).thenReturn(lancamentoMock);
        when(lancamentoBusiness.entidadeParaComprovanteResponseDTO(eq(lancamentoMock))).thenReturn(buildComprovanteResponseDTO());

        ComprovanteResponseDTO comprovanteResponse = contaBusiness.efetuaLancamento(contaMock.getId(), lancamentoRequest, tipoLancamento);

        verify(contaRepository).findById(eq(contaMock.getId()));
        verify(contaRepository).save(eq(contaMock));
        verify(lancamentoBusiness).efetuaLancamento(eq(lancamentoRequest), eq(contaMock), eq(natureza), eq(tipoLancamento));
        verifyNoMoreInteractions(contaRepository);
        verifyNoMoreInteractions(lancamentoBusiness);

        assertEquals(saldoContaAntes.add(valor), contaMock.getSaldo());
        assertNotNull(comprovanteResponse);
    }

    @Test
    public void efetuaLancamentoContaInexistente() {
        TipoLancamento tipoLancamento = TipoLancamento.DEPOSITO;
        BigDecimal valor = BigDecimal.valueOf(100);
        Long idConta = 1L;
        LancamentoRequestDTO lancamentoRequest = buildLancamentoRequestDTO(valor);

        when(contaRepository.findById(eq(idConta))).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> contaBusiness.efetuaLancamento(idConta, lancamentoRequest, tipoLancamento));

        verify(contaRepository).findById(eq(idConta));
        verifyNoMoreInteractions(contaRepository);
        verifyNoInteractions(lancamentoBusiness);

        assertEquals("DB-3", businessException.getCodigoErro());
    }

    @Test
    public void efetuaTransferenciaComSucesso() {
        TipoLancamento tipoLancamento = TipoLancamento.TRANSFERENCIA;
        BigDecimal valor = BigDecimal.valueOf(100);

        Conta contaDebitoMock = buildContaEntidade();
        Conta contaCreditoMock = buildContaEntidade();
        contaCreditoMock.setNumeroAgencia(1);
        contaCreditoMock.setNumeroConta(102031L);

        BigDecimal saldoContaDebitoAntes = contaDebitoMock.getSaldo();
        BigDecimal saldoContaCreditoAntes = contaCreditoMock.getSaldo();

        Lancamento lancamentoDebitoMock = buildLancamentoEntidade(tipoLancamento, Natureza.DEBITO, valor);
        Lancamento lancamentoCreditoMock = buildLancamentoEntidade(tipoLancamento, Natureza.CREDITO, valor);

        TransferenciaRequestDTO transferenciaRequest = buildTransferenciaRequestDTO(contaCreditoMock.getNumeroAgencia(), contaCreditoMock.getNumeroConta(), valor);

        when(contaRepository.findById(eq(contaDebitoMock.getId()))).thenReturn(Optional.of(contaDebitoMock));
        when(contaRepository.findByNumeroAgenciaAndNumeroConta(eq(contaCreditoMock.getNumeroAgencia()), eq(contaCreditoMock.getNumeroConta()))).thenReturn(Optional.of(contaCreditoMock));
        when(lancamentoBusiness.efetuaLancamento(any(LancamentoRequestDTO.class), eq(contaDebitoMock), eq(Natureza.DEBITO), eq(tipoLancamento))).thenReturn(lancamentoDebitoMock);
        when(lancamentoBusiness.efetuaLancamento(any(LancamentoRequestDTO.class), eq(contaCreditoMock), eq(Natureza.CREDITO), eq(tipoLancamento))).thenReturn(lancamentoCreditoMock);
        when(lancamentoBusiness.efetuaTransferencia(eq(lancamentoDebitoMock), eq(lancamentoCreditoMock))).thenReturn(buildComprovanteResponseDTO());

        ComprovanteResponseDTO comprovanteResponse = contaBusiness.efetuaTransferencia(contaDebitoMock.getId(), transferenciaRequest);

        verify(contaRepository).findById(eq(contaDebitoMock.getId()));
        verify(contaRepository).findByNumeroAgenciaAndNumeroConta(eq(contaCreditoMock.getNumeroAgencia()), eq(contaCreditoMock.getNumeroConta()));
        verify(lancamentoBusiness).efetuaLancamento(any(LancamentoRequestDTO.class), eq(contaDebitoMock), eq(Natureza.DEBITO), eq(TipoLancamento.TRANSFERENCIA));
        verify(lancamentoBusiness).efetuaLancamento(any(LancamentoRequestDTO.class), eq(contaCreditoMock), eq(Natureza.CREDITO), eq(TipoLancamento.TRANSFERENCIA));
        verify(contaRepository).save(eq(contaDebitoMock));
        verify(contaRepository).save(eq(contaCreditoMock));
        verify(lancamentoBusiness).efetuaTransferencia(eq(lancamentoDebitoMock), eq(lancamentoCreditoMock));
        verifyNoMoreInteractions(contaRepository);
        verifyNoMoreInteractions(lancamentoBusiness);

        assertEquals(saldoContaDebitoAntes.subtract(valor), contaDebitoMock.getSaldo());
        assertEquals(saldoContaCreditoAntes.add(valor), contaCreditoMock.getSaldo());
        assertNotNull(comprovanteResponse);
    }

    @Test
    public void efetuaTransferenciaContaDebitoInexistente() {
        BigDecimal valor = BigDecimal.valueOf(100);

        Conta contaDebitoMock = buildContaEntidade();
        Conta contaCreditoMock = buildContaEntidade();
        contaCreditoMock.setNumeroAgencia(1);
        contaCreditoMock.setNumeroConta(102031L);

        TransferenciaRequestDTO transferenciaRequest = buildTransferenciaRequestDTO(contaCreditoMock.getNumeroAgencia(), contaCreditoMock.getNumeroConta(), valor);

        when(contaRepository.findById(eq(contaDebitoMock.getId()))).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> contaBusiness.efetuaTransferencia(contaDebitoMock.getId(), transferenciaRequest));

        verify(contaRepository).findById(eq(contaDebitoMock.getId()));
        verifyNoMoreInteractions(contaRepository);
        verifyNoInteractions(lancamentoBusiness);

        assertEquals("DB-3", businessException.getCodigoErro());
    }

    @Test
    public void efetuaTransferenciaContaCreditoInexistente() {
        BigDecimal valor = BigDecimal.valueOf(100);

        Conta contaDebitoMock = buildContaEntidade();
        Conta contaCreditoMock = buildContaEntidade();
        contaCreditoMock.setNumeroAgencia(1);
        contaCreditoMock.setNumeroConta(102031L);

        TransferenciaRequestDTO transferenciaRequest = buildTransferenciaRequestDTO(contaCreditoMock.getNumeroAgencia(), contaCreditoMock.getNumeroConta(), valor);

        when(contaRepository.findById(eq(contaDebitoMock.getId()))).thenReturn(Optional.of(contaDebitoMock));
        when(contaRepository.findByNumeroAgenciaAndNumeroConta(eq(contaCreditoMock.getNumeroAgencia()), eq(contaCreditoMock.getNumeroConta()))).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> contaBusiness.efetuaTransferencia(contaDebitoMock.getId(), transferenciaRequest));

        verify(contaRepository).findById(eq(contaDebitoMock.getId()));
        verify(contaRepository).findByNumeroAgenciaAndNumeroConta(eq(contaCreditoMock.getNumeroAgencia()), eq(contaCreditoMock.getNumeroConta()));
        verifyNoMoreInteractions(contaRepository);
        verifyNoInteractions(lancamentoBusiness);

        assertEquals("DB-5", businessException.getCodigoErro());
    }

    @Test
    public void consultaExtratoCompletoComSucesso() {
        Conta contaMock = buildContaEntidade();
        List<ComprovanteResponseDTO> comprovantes = Arrays.asList(buildComprovanteResponseDTO(), buildComprovanteResponseDTO());

        when(contaRepository.findById(eq(contaMock.getId()))).thenReturn(Optional.of(contaMock));
        when(lancamentoBusiness.consultaExtratoCompleto(eq(contaMock))).thenReturn(comprovantes);

        ExtratoResponseDTO extratoResponse = contaBusiness.consultaExtratoCompleto(contaMock.getId());

        verify(contaRepository).findById(eq(contaMock.getId()));
        verify(lancamentoBusiness).consultaExtratoCompleto(eq(contaMock));
        verifyNoMoreInteractions(contaRepository);
        verifyNoMoreInteractions(lancamentoBusiness);

        assertEquals(comprovantes.size(), extratoResponse.getLancamentos().size());
    }

    @Test
    public void consultaExtratoCompletoContaInexistente() {
        Conta contaMock = buildContaEntidade();

        when(contaRepository.findById(eq(contaMock.getId()))).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> contaBusiness.consultaExtratoCompleto(contaMock.getId()));

        verify(contaRepository).findById(eq(contaMock.getId()));
        verifyNoMoreInteractions(contaRepository);
        verifyNoInteractions(lancamentoBusiness);

        assertEquals("DB-3", businessException.getCodigoErro());
    }

    @Test
    public void consultaExtratoPeriodoComSucesso() {
        Conta contaMock = buildContaEntidade();
        List<ComprovanteResponseDTO> comprovantes = Arrays.asList(buildComprovanteResponseDTO(), buildComprovanteResponseDTO());
        LocalDate dataInicial = LocalDate.now();
        LocalDate dataFinal = LocalDate.now();

        when(contaRepository.findById(eq(contaMock.getId()))).thenReturn(Optional.of(contaMock));
        when(lancamentoBusiness.consultaExtratoPorPeriodo(eq(contaMock), eq(dataInicial), eq(dataFinal))).thenReturn(comprovantes);

        ExtratoResponseDTO extratoResponse = contaBusiness.consultaExtratoPorPeriodo(contaMock.getId(), dataInicial, dataFinal);

        verify(contaRepository).findById(eq(contaMock.getId()));
        verify(lancamentoBusiness).consultaExtratoPorPeriodo(eq(contaMock), eq(dataInicial), eq(dataFinal));
        verifyNoMoreInteractions(contaRepository);
        verifyNoMoreInteractions(lancamentoBusiness);

        assertEquals(comprovantes.size(), extratoResponse.getLancamentos().size());
    }

    @Test
    public void consultaExtratoPeriodoContaInexistente() {
        Conta contaMock = buildContaEntidade();
        LocalDate dataInicial = LocalDate.now();
        LocalDate dataFinal = LocalDate.now();

        when(contaRepository.findById(eq(contaMock.getId()))).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> contaBusiness.consultaExtratoPorPeriodo(contaMock.getId(), dataInicial, dataFinal));

        verify(contaRepository).findById(eq(contaMock.getId()));
        verifyNoMoreInteractions(contaRepository);
        verifyNoInteractions(lancamentoBusiness);

        assertEquals("DB-3", businessException.getCodigoErro());
    }

    @Test
    public void estornaLancamentoComSucesso() {
        Long idConta = 1L;
        Long idLancamento = 1L;

        when(lancamentoBusiness.estornaLancamento(eq(idConta), eq(idLancamento))).thenReturn(buildComprovanteResponseDTO());

        ComprovanteResponseDTO comprovanteResponse = contaBusiness.estornaLancamento(idConta, idLancamento);

        verify(lancamentoBusiness).estornaLancamento(eq(idConta), eq(idLancamento));
        verifyNoMoreInteractions(lancamentoBusiness);
        verifyNoInteractions(contaRepository);

        assertNotNull(comprovanteResponse);
    }

    @Test
    public void consultaComprovanteLancamentoComSucesso() {
        Long idConta = 1L;
        Long idLancamento = 1L;

        when(lancamentoBusiness.consultaComprovanteLancamento(eq(idConta), eq(idLancamento))).thenReturn(buildComprovanteResponseDTO());

        ComprovanteResponseDTO comprovanteResponse = contaBusiness.consultaComprovanteLancamento(idConta, idLancamento);

        verify(lancamentoBusiness).consultaComprovanteLancamento(eq(idConta), eq(idLancamento));
        verifyNoMoreInteractions(lancamentoBusiness);
        verifyNoInteractions(contaRepository);

        assertNotNull(comprovanteResponse);
    }

    @Test
    public void removeLancamentoEstornoComSucesso() {
        Long idConta = 1L;
        Long idLancamento = 1L;

        contaBusiness.removeLancamentoEstorno(idConta, idLancamento);

        verify(lancamentoBusiness).removeLancamentoEstorno(eq(idConta), eq(idLancamento));
        verifyNoMoreInteractions(lancamentoBusiness);
        verifyNoInteractions(contaRepository);
    }

    @Test
    public void consultaTodasAsContasComSucesso() {
        List<Conta> contasMock = Arrays.asList(buildContaEntidade(), buildContaEntidade());

        when(contaRepository.findAll()).thenReturn(contasMock);

        List<ContaResponseDTO> contasResponse = contaBusiness.consultaTodas();

        verify(contaRepository).findAll();
        verifyNoMoreInteractions(contaRepository);
        verifyNoInteractions(lancamentoBusiness);

        assertEquals(contasMock.size(), contasResponse.size());
    }

    @Test
    public void consultaContaPorIdClienteComSucesso() {
        Conta contaMock = buildContaEntidade();

        when(contaRepository.findByCliente_Id(eq(contaMock.getCliente().getId()))).thenReturn(Optional.of(contaMock));

        ContaResponseDTO contaResponse = contaBusiness.consultaContaPorIdCliente(contaMock.getCliente().getId());

        verify(contaRepository).findByCliente_Id(eq(contaMock.getCliente().getId()));
        verifyNoMoreInteractions(contaRepository);
        verifyNoInteractions(lancamentoBusiness);

        assertNotNull(contaResponse);
    }

    @Test
    public void consultaContaPorIdClienteInexistente() {
        Conta contaMock = buildContaEntidade();

        when(contaRepository.findByCliente_Id(eq(contaMock.getCliente().getId()))).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> contaBusiness.consultaContaPorIdCliente(contaMock.getCliente().getId()));

        verify(contaRepository).findByCliente_Id(eq(contaMock.getCliente().getId()));
        verifyNoMoreInteractions(contaRepository);
        verifyNoInteractions(lancamentoBusiness);

        assertEquals("DB-12", businessException.getCodigoErro());
    }

    @Test
    public void bloqueiaContaComSucesso() {
        Conta contaMock = buildContaEntidade();

        when(contaRepository.findById(eq(contaMock.getId()))).thenReturn(Optional.of(contaMock));

        contaBusiness.bloqueiaConta(contaMock.getId());

        verify(contaRepository).findById(eq(contaMock.getId()));
        verify(contaRepository).save(eq(contaMock));
        verifyNoMoreInteractions(contaRepository);
        verifyNoInteractions(lancamentoBusiness);

        assertEquals(SituacaoConta.BLOQUEADA.getCodigo(), contaMock.getSituacao());
    }

    @Test
    public void bloqueiaContaInexistente() {
        Conta contaMock = buildContaEntidade();

        when(contaRepository.findById(eq(contaMock.getId()))).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> contaBusiness.bloqueiaConta(contaMock.getId()));

        verify(contaRepository).findById(eq(contaMock.getId()));
        verifyNoMoreInteractions(contaRepository);
        verifyNoInteractions(lancamentoBusiness);

        assertEquals("DB-3", businessException.getCodigoErro());
    }

    @Test
    public void bloqueiaContaJaBloqueada() {
        Conta contaMock = buildContaEntidade();
        contaMock.setSituacao(SituacaoConta.BLOQUEADA.getCodigo());

        when(contaRepository.findById(eq(contaMock.getId()))).thenReturn(Optional.of(contaMock));

        BusinessException businessException = assertThrows(BusinessException.class, () -> contaBusiness.bloqueiaConta(contaMock.getId()));

        verify(contaRepository).findById(eq(contaMock.getId()));
        verifyNoMoreInteractions(contaRepository);
        verifyNoInteractions(lancamentoBusiness);

        assertEquals("DB-13", businessException.getCodigoErro());
    }

    @Test
    public void desbloqueiaContaComSucesso() {
        Conta contaMock = buildContaEntidade();
        contaMock.setSituacao(SituacaoConta.BLOQUEADA.getCodigo());

        when(contaRepository.findById(eq(contaMock.getId()))).thenReturn(Optional.of(contaMock));

        contaBusiness.desbloqueiaConta(contaMock.getId());

        verify(contaRepository).findById(eq(contaMock.getId()));
        verify(contaRepository).save(eq(contaMock));
        verifyNoMoreInteractions(contaRepository);
        verifyNoInteractions(lancamentoBusiness);

        assertEquals(SituacaoConta.ABERTA.getCodigo(), contaMock.getSituacao());
    }

    @Test
    public void desbloqueiaContaInexistente() {
        Conta contaMock = buildContaEntidade();

        when(contaRepository.findById(eq(contaMock.getId()))).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> contaBusiness.desbloqueiaConta(contaMock.getId()));

        verify(contaRepository).findById(eq(contaMock.getId()));
        verifyNoMoreInteractions(contaRepository);
        verifyNoInteractions(lancamentoBusiness);

        assertEquals("DB-3", businessException.getCodigoErro());
    }

    @Test
    public void desbloqueiaContaJaAberta() {
        Conta contaMock = buildContaEntidade();

        when(contaRepository.findById(eq(contaMock.getId()))).thenReturn(Optional.of(contaMock));

        BusinessException businessException = assertThrows(BusinessException.class, () -> contaBusiness.desbloqueiaConta(contaMock.getId()));

        verify(contaRepository).findById(eq(contaMock.getId()));
        verifyNoMoreInteractions(contaRepository);
        verifyNoInteractions(lancamentoBusiness);

        assertEquals("DB-14", businessException.getCodigoErro());
    }

    @Test
    public void realizaSorteioComSucesso() {
        TipoLancamento tipoLancamento = TipoLancamento.DEPOSITO;
        Natureza natureza = Natureza.CREDITO;
        BigDecimal valorSorteio = BigDecimal.valueOf(1000);

        Conta conta1 = buildContaEntidade();
        conta1.setId(1L);

        Conta conta2 = buildContaEntidade();
        conta2.setId(2L);

        List<Conta> contasMock = Arrays.asList(conta1, conta2);

        BigDecimal saldoContaAntes = conta1.getSaldo();
        Lancamento lancamentoMock = buildLancamentoEntidade(tipoLancamento, natureza, valorSorteio);

        when(contaRepository.findBySituacao(eq(SituacaoConta.ABERTA.getCodigo()))).thenReturn(contasMock);
        when(lancamentoBusiness.efetuaLancamento(any(LancamentoRequestDTO.class), any(Conta.class), eq(natureza), eq(tipoLancamento))).thenReturn(lancamentoMock);

        Long idContaSorteada = contaBusiness.realizaSorteio();

        verify(contaRepository).findBySituacao(eq(SituacaoConta.ABERTA.getCodigo()));
        verify(lancamentoBusiness).efetuaLancamento(any(LancamentoRequestDTO.class), any(Conta.class), eq(natureza), eq(tipoLancamento));
        verify(contaRepository).save(any(Conta.class));
        verifyNoMoreInteractions(contaRepository);
        verifyNoMoreInteractions(lancamentoBusiness);

        assertNotNull(idContaSorteada);
        Conta contaMockSorteada = contasMock.stream().filter(conta -> idContaSorteada.equals(conta.getId())).findFirst().orElse(null);
        assertNotNull(contaMockSorteada);
        assertEquals(saldoContaAntes.add(valorSorteio), contaMockSorteada.getSaldo());
    }

    @Test
    public void realizaSorteioSemContasSituacaoAberta() {
        List<Conta> contasMock = Collections.emptyList();

        when(contaRepository.findBySituacao(eq(SituacaoConta.ABERTA.getCodigo()))).thenReturn(contasMock);

        Long idContaSorteada = contaBusiness.realizaSorteio();

        verify(contaRepository).findBySituacao(eq(SituacaoConta.ABERTA.getCodigo()));
        verifyNoMoreInteractions(contaRepository);
        verifyNoInteractions(lancamentoBusiness);

        assertNull(idContaSorteada);
    }

}
