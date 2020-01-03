package com.matera.cursoferias.digitalbank.controller;

import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildClienteRequestDTO;
import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildGetRequestWithSpec;
import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildPostRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
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

		efetuaDeposito(valor, descricao).
			root("dados").
				body("idLancamento", greaterThan(0)).
				body("codigoAutenticacao", notNullValue()).
				body("dataHora", notNullValue()).
				body("valor", equalTo(valor.intValue())).
				body("natureza", equalTo(Natureza.CREDITO.getCodigo())).
				body("tipoLancamento", equalTo(TipoLancamento.DEPOSITO.getCodigo())).
				body("descricao", equalTo(descricao));

		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("id", contaResponse.getDados().getIdCliente()).
			build();

		buildGetRequestWithSpec(requestSpecification, "digitalbank/api/v1/clientes/{id}/conta", HttpStatus.OK).
			root("dados").
				body("saldo", equalTo(valor.floatValue()));
	}

	@Test
	public void efetuaSaqueTest() {
		efetuaDeposito(new BigDecimal(100), "Depósito");

		LancamentoRequestDTO lancamento = new LancamentoRequestDTO();
		lancamento.setValor(new BigDecimal(50));
		lancamento.setDescricao("Saque");

		buildPostRequest(lancamento, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/sacar", HttpStatus.OK).
			root("dados").
				body("idLancamento", greaterThan(0)).
				body("codigoAutenticacao", notNullValue()).
				body("dataHora", notNullValue()).
				body("valor", equalTo(lancamento.getValor().intValue())).
				body("natureza", equalTo(Natureza.DEBITO.getCodigo())).
				body("tipoLancamento", equalTo(TipoLancamento.SAQUE.getCodigo())).
				body("descricao", equalTo(lancamento.getDescricao()));

		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("id", contaResponse.getDados().getIdCliente()).
			build();

		buildGetRequestWithSpec(requestSpecification, "digitalbank/api/v1/clientes/{id}/conta", HttpStatus.OK).
			root("dados").
				body("saldo", equalTo(lancamento.getValor().floatValue()));
	}

	@Test
	public void efetuaPagamentoTest() {
		efetuaDeposito(new BigDecimal(100), "Depósito");

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

		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("id", contaResponse.getDados().getIdCliente()).
			build();

		buildGetRequestWithSpec(requestSpecification, "digitalbank/api/v1/clientes/{id}/conta", HttpStatus.OK).
			root("dados").
				body("saldo", equalTo(lancamento.getValor().floatValue()));
	}
	
	@Test
	public void efetuaTransferenciaTest() {
		efetuaDeposito(new BigDecimal(100), "Depósito");

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

		RequestSpecification requestSpecificationOrigem = new RequestSpecBuilder().
			addPathParam("id", contaResponse.getDados().getIdCliente()).
			build();

		buildGetRequestWithSpec(requestSpecificationOrigem, "digitalbank/api/v1/clientes/{id}/conta", HttpStatus.OK).
			root("dados").
				body("saldo", equalTo(70));
		
		RequestSpecification requestSpecificationDestino = new RequestSpecBuilder().
			addPathParam("id", contaDestino.getDados().getIdCliente()).
			build();

		buildGetRequestWithSpec(requestSpecificationDestino, "digitalbank/api/v1/clientes/{id}/conta", HttpStatus.OK).
			root("dados").
				body("saldo", equalTo(30));
	}

	private ValidatableResponse efetuaDeposito(BigDecimal valor, String descricao) {
		LancamentoRequestDTO lancamento = new LancamentoRequestDTO();
		lancamento.setValor(valor);
		lancamento.setDescricao(descricao);

		return buildPostRequest(lancamento, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/depositar", HttpStatus.OK);
	}
}
