package com.matera.cursoferias.digitalbank.integration;

import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildClienteRequestDTO;
import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildGetRequest;
import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildPostRequest;
import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildPutRequest;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.matera.cursoferias.digitalbank.domain.enumerator.SituacaoConta;
import com.matera.cursoferias.digitalbank.dto.request.ClienteRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ResponseDTO;
import com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.mapper.TypeRef;
import io.restassured.specification.RequestSpecification;

@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class ClienteIntegrationTest {

	private static final String URL_BASE = "digitalbank/api/v1/clientes";

	@Test
	public void cadastraClienteSucessoTest() {
		ClienteRequestDTO cliente = buildClienteRequestDTO();

		buildPostRequest(cliente, URL_BASE, HttpStatus.CREATED).
			root("dados").
				body("idCliente", greaterThan(0)).
				body("idConta", greaterThan(0)).
				body("numeroAgencia", greaterThan(0)).
				body("numeroConta", equalTo(cliente.getTelefone().intValue())).
				body("situacao", equalTo(SituacaoConta.ABERTA.getCodigo())).
				body("saldo", equalTo(BigDecimal.ZERO.intValue()));
	}
	
	@Test
	public void cadastraClienteJaExistenteTest() {
		ClienteRequestDTO cliente = buildClienteRequestDTO();

		buildPostRequest(cliente, URL_BASE, HttpStatus.CREATED);
		
		ClienteRequestDTO clienteJaExistente = buildClienteRequestDTO();
		
		buildPostRequest(clienteJaExistente, URL_BASE, HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-2"));
	}

	@Test
	public void consultaClientePorIdSucessoTest() {
		ClienteRequestDTO cliente = buildClienteRequestDTO();

		ResponseDTO<ContaResponseDTO> response = buildPostRequest(cliente, URL_BASE, HttpStatus.CREATED).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ContaResponseDTO>>() {});

		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("id", response.getDados().getIdCliente()).
			build();

		buildGetRequest(requestSpecification, URL_BASE + "/{id}", HttpStatus.OK);
	}
	
	@Test
	public void consultaClientePorIdNaoEncontradoTest() {
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("id", 1).
			build();

		buildGetRequest(requestSpecification, URL_BASE + "/{id}", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-1"));
	}

	@Test
	public void consultaContaPorIdClienteSucessoTest() {
		ClienteRequestDTO cliente = buildClienteRequestDTO();

		ResponseDTO<ContaResponseDTO> response = buildPostRequest(cliente, URL_BASE, HttpStatus.CREATED).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ContaResponseDTO>>() {});

		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("id", response.getDados().getIdCliente()).
			build();

		buildGetRequest(requestSpecification, URL_BASE + "/{id}/conta", HttpStatus.OK).
			root("dados").
				body("idCliente", equalTo(response.getDados().getIdCliente().intValue())).
				body("idConta", equalTo(response.getDados().getIdConta().intValue())).
				body("numeroAgencia", equalTo(response.getDados().getNumeroAgencia())).
				body("numeroConta", equalTo(response.getDados().getNumeroConta().intValue())).
				body("situacao", equalTo(response.getDados().getSituacao())).
				body("saldo", equalTo(response.getDados().getSaldo().floatValue()));
	}
	
	@Test
	public void consultaContaPorIdClienteNaoCadastradaTest() {
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("id", 1).
			build();

		buildGetRequest(requestSpecification, URL_BASE + "/{id}/conta", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-12"));
	}

	@Test
	public void consultaTodosClientesSucessoTest() {
		ClienteRequestDTO cliente1 = buildClienteRequestDTO();

		ClienteRequestDTO cliente2 = buildClienteRequestDTO();
		cliente2.setCpf("57573694695");
		cliente2.setTelefone(997242244L);

		buildPostRequest(cliente1, URL_BASE, HttpStatus.CREATED);
		buildPostRequest(cliente2, URL_BASE, HttpStatus.CREATED);

		DigitalBankTestUtils.buildGetRequest(URL_BASE, HttpStatus.OK).
			body("dados", hasSize(2));
	}
	
	@Test
	public void consultaTodosClientesNaoExisteSucessoTest() {
		DigitalBankTestUtils.buildGetRequest(URL_BASE, HttpStatus.OK).
			body("dados", hasSize(0));
	}

	@Test
	public void atualizaClienteSucessoTest() {
		ClienteRequestDTO cliente = buildClienteRequestDTO();

		ResponseDTO<ContaResponseDTO> response = buildPostRequest(cliente, URL_BASE, HttpStatus.CREATED).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ContaResponseDTO>>() {});

		cliente.setNome("Pedro da Silva");
		cliente.setCpf("57573694695");
		cliente.setTelefone(997242244L);
		cliente.setRendaMensal(BigDecimal.valueOf(5000));
		cliente.setLogradouro("Avenida Paulista");
		cliente.setNumero(100);
		cliente.setComplemento("Casa");
		cliente.setBairro("Paulista");
		cliente.setCidade("São Paulo");
		cliente.setEstado("SP");
		cliente.setCep("73887445");

		buildPutRequest(cliente, URL_BASE + "/" + response.getDados().getIdCliente(), HttpStatus.NO_CONTENT);

		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("id", response.getDados().getIdCliente()).
			build();

		buildGetRequest(requestSpecification, URL_BASE + "/{id}", HttpStatus.OK).
			root("dados").
				body("nome", equalTo(cliente.getNome())).
				body("cpf", equalTo(cliente.getCpf())).
				body("telefone", equalTo(cliente.getTelefone().intValue())).
				body("rendaMensal", equalTo(cliente.getRendaMensal().floatValue())).
				body("logradouro", equalTo(cliente.getLogradouro())).
				body("numero", equalTo(cliente.getNumero())).
				body("complemento", equalTo(cliente.getComplemento())).
				body("bairro", equalTo(cliente.getBairro())).
				body("cidade", equalTo(cliente.getCidade())).
				body("estado", equalTo(cliente.getEstado())).
				body("cep", equalTo(cliente.getCep()));
	}
	
	@Test
	public void atualizaClienteNaoEncontradoTest() {
		ClienteRequestDTO cliente = buildClienteRequestDTO();

		buildPutRequest(cliente, URL_BASE + "/" + 1, HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-1"));
	}

}