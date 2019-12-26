package com.matera.cursoferias.digitalbank.controller;

import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildGetRequestWithSpec;
import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildPostRequest;
import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildPutRequest;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

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
public class ClienteIntegrationTest {

	private static final String URL = "digitalbank/api/v1/clientes";

	@Test
	public void cadastraClienteTest() {
		ClienteRequestDTO cliente = buildClienteRequestDTO();

		buildPostRequest(cliente, URL, HttpStatus.CREATED).
			root("dados").
				body("numeroConta", equalTo(cliente.getTelefone().intValue())).
				body("situacao", equalTo(SituacaoConta.ABERTA.getCodigo())).
				body("saldo", equalTo(BigDecimal.ZERO.intValue()));
	}

	@Test
	public void consultaClientePorIdTest() {
		ClienteRequestDTO cliente = buildClienteRequestDTO();

		ResponseDTO<ContaResponseDTO> response = buildPostRequest(cliente, URL, HttpStatus.CREATED).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ContaResponseDTO>>() {});

		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addQueryParam("id", response.getDados().getIdCliente()).
			build();

		buildGetRequestWithSpec(requestSpecification, URL, HttpStatus.OK);
	}

	@Test
	public void consultaContaPorIdClienteTest() {
		ClienteRequestDTO cliente = buildClienteRequestDTO();

		ResponseDTO<ContaResponseDTO> response = buildPostRequest(cliente, URL, HttpStatus.CREATED).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ContaResponseDTO>>() {});

		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("id", response.getDados().getIdCliente()).
			build();

		buildGetRequestWithSpec(requestSpecification, URL + "/{id}/conta", HttpStatus.OK).
			root("dados").
				body("idCliente", equalTo(response.getDados().getIdCliente().intValue())).
				body("idConta", equalTo(response.getDados().getIdConta().intValue())).
				body("numeroAgencia", equalTo(response.getDados().getNumeroAgencia())).
				body("numeroConta", equalTo(response.getDados().getNumeroConta().intValue())).
				body("situacao", equalTo(response.getDados().getSituacao()));
				// TODO - falta comparar o saldo
	}

	@Test
	public void consultaTodosClientesTest() {
		ClienteRequestDTO cliente1 = buildClienteRequestDTO();

		ClienteRequestDTO cliente2 = buildClienteRequestDTO();
		cliente2.setCpf("57573694695");
		cliente2.setTelefone(997242244L);

		buildPostRequest(cliente1, URL, HttpStatus.CREATED);
		buildPostRequest(cliente2, URL, HttpStatus.CREATED);

		DigitalBankTestUtils.buildGetRequest(URL, HttpStatus.OK).
			body("dados.size()", equalTo(2));
	}

	@Test
	public void atualizaClienteTest() {
		ClienteRequestDTO cliente = buildClienteRequestDTO();

		ResponseDTO<ContaResponseDTO> response = buildPostRequest(cliente, URL, HttpStatus.CREATED).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ContaResponseDTO>>() {});

		cliente.setNome("Pedro da Silva");
		cliente.setCpf("57573694695");
		cliente.setTelefone(997242244L);
		cliente.setRendaMensal(new BigDecimal(5000));
		cliente.setLogradouro("Avenida Paulista");
		cliente.setNumero(100);
		cliente.setComplemento("Casa");
		cliente.setBairro("Paulista");
		cliente.setCidade("São Paulo");
		cliente.setEstado("SP");
		cliente.setCep("73887445");

		buildPutRequest(cliente, URL + "/" + response.getDados().getIdCliente(), HttpStatus.NO_CONTENT);

		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addQueryParam("id", response.getDados().getIdCliente()).
			build();

		buildGetRequestWithSpec(requestSpecification, URL, HttpStatus.OK).
			root("dados").
				body("nome", hasItem(cliente.getNome())).
				body("cpf", hasItem(cliente.getCpf())).
				body("telefone", hasItem(cliente.getTelefone().intValue())).
//				body("rendaMensal", hasItem(cliente.getRendaMensal())).
				body("logradouro", hasItem(cliente.getLogradouro())).
				body("numero", hasItem(cliente.getNumero())).
				body("complemento", hasItem(cliente.getComplemento())).
				body("bairro", hasItem(cliente.getBairro())).
				body("cidade", hasItem(cliente.getCidade())).
				body("estado", hasItem(cliente.getEstado())).
				body("cep", hasItem(cliente.getCep()));
	}

	private ClienteRequestDTO buildClienteRequestDTO() {
		return ClienteRequestDTO.builder().
			nome("João da Silva").
			cpf("05728520022").
			telefone(997542877L).
			rendaMensal(new BigDecimal(10000)).
			logradouro("Avenida São Paulo").
			numero(1287).
			complemento("Apto 207").
			bairro("Centro").
			cidade("Maringá").
			estado("PR").
			cep("87005002").
			build();
	}
}
