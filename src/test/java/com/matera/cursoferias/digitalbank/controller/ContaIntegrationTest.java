package com.matera.cursoferias.digitalbank.controller;

import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildClienteRequestDTO;
import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildGetRequest;
import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildPostRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.matera.cursoferias.digitalbank.domain.enumerator.Natureza;
import com.matera.cursoferias.digitalbank.domain.enumerator.SituacaoConta;
import com.matera.cursoferias.digitalbank.domain.enumerator.TipoLancamento;
import com.matera.cursoferias.digitalbank.dto.request.ClienteRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.TransferenciaRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ResponseDTO;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.mapper.TypeRef;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class ContaIntegrationTest {

	private static final String URL_BASE = "digitalbank/api/v1/contas";

	private ResponseDTO<ContaResponseDTO> contaResponse;

	@BeforeEach
	public void buildCliente() {
		ClienteRequestDTO clienteRequest = buildClienteRequestDTO();

		contaResponse = buildPostRequest(clienteRequest, "digitalbank/api/v1/clientes", HttpStatus.CREATED).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ContaResponseDTO>>() {});
	}

	@Test
	public void efetuaDepositoTest() {
		BigDecimal valor = new BigDecimal(100);
		String descricao = "Depósito";
		
		efetuaDeposito(new BigDecimal(100), "Depósito", HttpStatus.OK).
			root("dados").
				body("idLancamento", greaterThan(0)).
				body("codigoAutenticacao", notNullValue()).
				body("dataHora", notNullValue()).
				body("valor", equalTo(valor.intValue())).
				body("natureza", equalTo(Natureza.CREDITO.getCodigo())).
				body("tipoLancamento", equalTo(TipoLancamento.DEPOSITO.getCodigo())).
				body("descricao", equalTo(descricao));

		consultaConta(contaResponse.getDados().getIdCliente()).
			root("dados").
				body("saldo", equalTo(valor.floatValue()));
	}
	
	@Test
	public void efetuaDepositoContaNaoEncontradaTest() {
		efetuaLancamentoErro("/2/depositar", "DB-3");
	}
	
	@Test
	public void efetuaDepositoContaBloqueadaTest() {
		bloqueiaConta(contaResponse.getDados().getIdConta(), HttpStatus.NO_CONTENT);
			
		efetuaLancamentoErro("/" + contaResponse.getDados().getIdConta() + "/depositar", "DB-15");
	}

	@Test
	public void efetuaSaqueTest() {
		efetuaDeposito(new BigDecimal(100), "Depósito", HttpStatus.OK);

		LancamentoRequestDTO saque = new LancamentoRequestDTO();
		saque.setValor(new BigDecimal(50));
		saque.setDescricao("Saque");

		buildPostRequest(saque, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/sacar", HttpStatus.OK).
			root("dados").
				body("idLancamento", greaterThan(0)).
				body("codigoAutenticacao", notNullValue()).
				body("dataHora", notNullValue()).
				body("valor", equalTo(saque.getValor().intValue())).
				body("natureza", equalTo(Natureza.DEBITO.getCodigo())).
				body("tipoLancamento", equalTo(TipoLancamento.SAQUE.getCodigo())).
				body("descricao", equalTo(saque.getDescricao()));

		consultaConta(contaResponse.getDados().getIdCliente()).
			root("dados").
				body("saldo", equalTo(saque.getValor().floatValue()));
	}
	
	@Test
	public void efetuaSaqueContaNaoEncontradaTest() {
		efetuaLancamentoErro("/2/sacar", "DB-3");
	}
	
	@Test
	public void efetuaSaqueContaBloqueadaTest() {
		efetuaDeposito(new BigDecimal(100), "Depósito", HttpStatus.OK);

		bloqueiaConta(contaResponse.getDados().getIdConta(), HttpStatus.NO_CONTENT);
		
		efetuaLancamentoErro("/" + contaResponse.getDados().getIdConta() + "/sacar", "DB-15");
	}
	
	@Test
	public void efetuaSaqueContaSemSaldoTest() {
		efetuaLancamentoErro("/" + contaResponse.getDados().getIdConta() + "/sacar", "DB-6");
	}

	@Test
	public void efetuaPagamentoTest() {
		efetuaDeposito(new BigDecimal(100), "Depósito", HttpStatus.OK);

		LancamentoRequestDTO lancamento = new LancamentoRequestDTO();
		lancamento.setValor(new BigDecimal(50));
		lancamento.setDescricao("Pagamento");

		buildPostRequest(lancamento, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/pagar", HttpStatus.OK).
			root("dados").
				body("idLancamento", greaterThan(0)).
				body("codigoAutenticacao", notNullValue()).
				body("dataHora", notNullValue()).
				body("valor", equalTo(lancamento.getValor().intValue())).
				body("natureza", equalTo(Natureza.DEBITO.getCodigo())).
				body("tipoLancamento", equalTo(TipoLancamento.PAGAMENTO.getCodigo())).
				body("descricao", equalTo(lancamento.getDescricao()));

		consultaConta(contaResponse.getDados().getIdCliente()).
			root("dados").
				body("saldo", equalTo(lancamento.getValor().floatValue()));
	}
	
	@Test
	public void efetuaPagamentoContaNaoEncontradaTest() {
		efetuaLancamentoErro("/2/pagar", "DB-3");
	}
	
	@Test
	public void efetuaPagamentoContaBloqueadaTest() {
		efetuaDeposito(new BigDecimal(100), "Depósito", HttpStatus.OK);

		bloqueiaConta(contaResponse.getDados().getIdConta(), HttpStatus.NO_CONTENT);
		
		efetuaLancamentoErro("/" + contaResponse.getDados().getIdConta() + "/pagar", "DB-15");
	}
	
	@Test
	public void efetuaPagamentoContaSemSaldoTest() {
		efetuaLancamentoErro("/" + contaResponse.getDados().getIdConta() + "/pagar", "DB-6");
	}
	
	@Test
	public void efetuaTransferenciaTest() {
		efetuaDeposito(new BigDecimal(100), "Depósito", HttpStatus.OK);

		ClienteRequestDTO clienteDestino = buildClienteRequestDTO();
		clienteDestino.setCpf("57573694695");
		clienteDestino.setTelefone(997242244L);

		ResponseDTO<ContaResponseDTO> contaDestino = buildPostRequest(clienteDestino, "digitalbank/api/v1/clientes", HttpStatus.CREATED).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ContaResponseDTO>>() {});

		TransferenciaRequestDTO transferencia = TransferenciaRequestDTO.
			builder().
				numeroAgencia(contaDestino.getDados().getNumeroAgencia()).
				numeroConta(contaDestino.getDados().getNumeroConta()).
				valor(new BigDecimal(30)).
				descricao("Transferência").
			build();
		
		buildPostRequest(transferencia, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/transferir", HttpStatus.OK).
			root("dados").
				body("idLancamento", greaterThan(0)).
				body("codigoAutenticacao", notNullValue()).
				body("dataHora", notNullValue()).
				body("valor", equalTo(transferencia.getValor().intValue())).
				body("natureza", equalTo(Natureza.DEBITO.getCodigo())).
				body("tipoLancamento", equalTo(TipoLancamento.TRANSFERENCIA.getCodigo())).
				body("descricao", equalTo(transferencia.getDescricao()));

		consultaConta(contaResponse.getDados().getIdCliente()).
			root("dados").
				body("saldo", equalTo(70f));
		
		consultaConta(contaDestino.getDados().getIdCliente()).
			root("dados").
				body("saldo", equalTo(30f));
	}

	@Test
	public void efetuaTransferenciaContaDebitoNaoEncontradaTest() {
		efetuaTransferenciaErro(1, 2L, "/2/transferir", "DB-3");
	}
	
	@Test
	public void efetuaTransferenciaContaCreditoNaoEncontradaTest() {
		efetuaTransferenciaErro(1, 2L, "/" + contaResponse.getDados().getIdConta() + "/transferir", "DB-5");
	}
	
	@Test
	public void efetuaTransferenciaContaDebitoBloqueadaTest() {
		efetuaDeposito(new BigDecimal(100), "Depósito", HttpStatus.OK);

		bloqueiaConta(contaResponse.getDados().getIdConta(), HttpStatus.NO_CONTENT);
		
		ClienteRequestDTO clienteDestino = buildClienteRequestDTO();
		clienteDestino.setCpf("57573694695");
		clienteDestino.setTelefone(997242244L);

		ResponseDTO<ContaResponseDTO> contaDestino = buildPostRequest(clienteDestino, "digitalbank/api/v1/clientes", HttpStatus.CREATED).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ContaResponseDTO>>() {});

		efetuaTransferenciaErro(contaDestino.getDados().getNumeroAgencia(), contaDestino.getDados().getNumeroConta(), "/" + contaResponse.getDados().getIdConta() + "/transferir", "DB-15");
	}
	
	@Test
	public void efetuaTransferenciaContaDebitoSemSaldoTest() {
		ClienteRequestDTO clienteDestino = buildClienteRequestDTO();
		clienteDestino.setCpf("57573694695");
		clienteDestino.setTelefone(997242244L);

		ResponseDTO<ContaResponseDTO> contaDestino = buildPostRequest(clienteDestino, "digitalbank/api/v1/clientes", HttpStatus.CREATED).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ContaResponseDTO>>() {});

		efetuaTransferenciaErro(contaDestino.getDados().getNumeroAgencia(), contaDestino.getDados().getNumeroConta(), "/" + contaResponse.getDados().getIdConta() + "/transferir", "DB-6");
	}
	
	@Test
	public void efetuaTransferenciaContaCreditoBloqueadaTest() {
		efetuaDeposito(new BigDecimal(100), "Depósito", HttpStatus.OK);

		ClienteRequestDTO clienteDestino = buildClienteRequestDTO();
		clienteDestino.setCpf("57573694695");
		clienteDestino.setTelefone(997242244L);

		ResponseDTO<ContaResponseDTO> contaDestino = buildPostRequest(clienteDestino, "digitalbank/api/v1/clientes", HttpStatus.CREATED).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ContaResponseDTO>>() {});

		bloqueiaConta(contaDestino.getDados().getIdConta(), HttpStatus.NO_CONTENT);

		efetuaTransferenciaErro(contaDestino.getDados().getNumeroAgencia(), contaDestino.getDados().getNumeroConta(), "/" + contaResponse.getDados().getIdConta() + "/transferir", "DB-15");
	}
	
	@Test
	public void bloqueiaContaTest() {
		bloqueiaConta(contaResponse.getDados().getIdConta(), HttpStatus.NO_CONTENT);
		
		consultaConta(contaResponse.getDados().getIdConta()).
			root("dados").
				body("situacao", equalTo(SituacaoConta.BLOQUEADA.getCodigo()));
	}
	
	@Test
	public void bloqueiaContaNaoEncontradaTest() {
		bloqueiaConta(2L, HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-3"));
	}
	
	@Test
	public void bloqueiaContaJaBloqueadaTest() {
		bloqueiaConta(contaResponse.getDados().getIdConta(), HttpStatus.NO_CONTENT);
		
		bloqueiaConta(contaResponse.getDados().getIdConta(), HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-13"));
	}
	
	@Test
	public void desbloqueiaContaTest() {
		bloqueiaConta(contaResponse.getDados().getIdConta(), HttpStatus.NO_CONTENT);
		
		desbloqueiaConta(contaResponse.getDados().getIdConta(), HttpStatus.NO_CONTENT);
		
		consultaConta(contaResponse.getDados().getIdConta()).
			root("dados").
				body("situacao", equalTo(SituacaoConta.ABERTA.getCodigo()));
	}
	
	@Test
	public void desbloqueiaContaNaoEncontradaTest() {
		desbloqueiaConta(2L, HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-3"));
	}
	
	@Test
	public void desbloqueiaContaJaBloqueadaTest() {
		bloqueiaConta(contaResponse.getDados().getIdConta(), HttpStatus.NO_CONTENT);
		
		desbloqueiaConta(contaResponse.getDados().getIdConta(), HttpStatus.NO_CONTENT);
		
		desbloqueiaConta(contaResponse.getDados().getIdConta(), HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-14"));
	}
	
	private ValidatableResponse efetuaDeposito(BigDecimal valor, String descricao, HttpStatus httpStatus) {
		LancamentoRequestDTO lancamento = new LancamentoRequestDTO();
		lancamento.setValor(valor);
		lancamento.setDescricao(descricao);
		
		return buildPostRequest(lancamento, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/depositar", httpStatus);
	}
	

	private ValidatableResponse bloqueiaConta(Long idConta, HttpStatus httpStatus) {
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("id", idConta).
			build();

		return buildPostRequest(requestSpecification, URL_BASE + "/{id}/bloquear", httpStatus);
	}
	
	private ValidatableResponse desbloqueiaConta(Long idConta, HttpStatus httpStatus) {
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("id", idConta).
			build();

		return buildPostRequest(requestSpecification, URL_BASE + "/{id}/desbloquear", httpStatus);
	}
	
	private void efetuaLancamentoErro(String url, String codigoErro) {
		LancamentoRequestDTO lancamento = new LancamentoRequestDTO();
		lancamento.setValor(new BigDecimal(50));
		lancamento.setDescricao("Lançamento");

		buildPostRequest(lancamento, URL_BASE + url, HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString(codigoErro));
	}
	
	private void efetuaTransferenciaErro(Integer numeroAgencia, Long numeroConta, String url, String codigoErro) {
		TransferenciaRequestDTO transferencia = TransferenciaRequestDTO.
			builder().
				numeroAgencia(numeroAgencia).
				numeroConta(numeroConta).
				valor(new BigDecimal(30)).
				descricao("Transferência").
			build();
		
		buildPostRequest(transferencia, URL_BASE + url, HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString(codigoErro));
	}
	
	private ValidatableResponse consultaConta(Long idConta) {
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("id", idConta).
			build();
	
		return buildGetRequest(requestSpecification, "digitalbank/api/v1/clientes/{id}/conta", HttpStatus.OK);
	}
}
