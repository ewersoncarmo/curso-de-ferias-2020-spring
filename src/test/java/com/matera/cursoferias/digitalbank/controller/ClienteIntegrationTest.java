package com.matera.cursoferias.digitalbank.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;

import com.matera.cursoferias.digitalbank.dto.request.ClienteRequestDTO;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class ClienteIntegrationTest {

	@Test
	public void cadastrarClienteTest() {
		ClienteRequestDTO cliente = new ClienteRequestDTO();
		cliente.setNome("Ewerson");
		cliente.setCpf("05223551966");
		cliente.setTelefone(997115453L);
		cliente.setRendaMensal(new BigDecimal(10000.00));
		cliente.setLogradouro("Rua");
		cliente.setNumero(159);
		cliente.setComplemento("Apto");
		cliente.setBairro("Malbec");
		cliente.setCidade("Maringá");
		cliente.setEstado("PR");
		cliente.setCep("87005002");
		
		given().
			spec(requestValida(cliente)).
			log().
				all().
		when().
			post("/api/v1/clientes").
		then().
			statusCode(HttpStatus.CREATED.value()).
			root("dado").
				body("numeroConta", equalTo(cliente.getTelefone().intValue())).
				body("saldo", equalTo(BigDecimal.ZERO.intValue()));
	}
	
	private RequestSpecification requestValida(Object body) {
		return new RequestSpecBuilder()
				.setContentType(ContentType.JSON)
				.addHeader("Accept", ContentType.JSON.toString())
				.setBody(body)
				.build();
	}
}
