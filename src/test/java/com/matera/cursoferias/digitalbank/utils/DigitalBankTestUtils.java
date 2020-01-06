package com.matera.cursoferias.digitalbank.utils;

import static io.restassured.RestAssured.given;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;

import com.matera.cursoferias.digitalbank.domain.entity.Cliente;
import com.matera.cursoferias.digitalbank.domain.enumerator.SituacaoConta;
import com.matera.cursoferias.digitalbank.dto.request.ClienteRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

public class DigitalBankTestUtils {

	private DigitalBankTestUtils() {}

	public static ValidatableResponse buildPostRequest(Object body, String url, HttpStatus httpStatus) {
		return given().
					spec(buildRequestSpecification(body)).
					log().
						all().
			   when().
			   		post(url).
			   then().
			   		statusCode(httpStatus.value());
	}

	public static ValidatableResponse buildPutRequest(Object body, String url, HttpStatus httpStatus) {
		return given().
					spec(buildRequestSpecification(body)).
					log().
						all().
			   when().
			   		put(url).
			   then().
			   		statusCode(httpStatus.value());
	}

	public static ValidatableResponse buildGetRequestWithSpec(RequestSpecification requestSpecification, String url, HttpStatus httpStatus) {
		return given().
					spec(requestSpecification).
					log().
						all().
			   when().
					get(url).
			   then().
					statusCode(httpStatus.value());
	}

	public static ValidatableResponse buildGetRequest(String url, HttpStatus httpStatus) {
		return given().
					log().
						all().
			   when().
					get(url).
			   then().
					statusCode(httpStatus.value());
	}

	private static RequestSpecification buildRequestSpecification(Object body) {
		return new RequestSpecBuilder().
			setContentType(ContentType.JSON).
			addHeader("Accept", ContentType.JSON.toString()).
			setBody(body).
			build();
	}

	public static ClienteRequestDTO buildClienteRequestDTO() {
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

	public static Cliente buildClienteEntidade() {
        return Cliente.builder().id(1L)
                                .nome("João da Silva")
                                .cpf("05728520022")
                                .telefone(997542877L)
                                .rendaMensal(new BigDecimal(10000))
                                .logradouro("Avenida São Paulo")
                                .numero(1287)
                                .complemento("Apto 207")
                                .bairro("Centro")
                                .cidade("Maringá")
                                .estado("PR")
                                .cep("87005002")
                                .build();
    }

	public static ContaResponseDTO buildContaResponseDTO() {
	    return ContaResponseDTO.builder().idCliente(1L)
	                                     .idConta(2L)
	                                     .numeroAgencia(1234)
	                                     .numeroConta(102030L)
	                                     .saldo(new BigDecimal(5000))
	                                     .situacao(SituacaoConta.ABERTA.getCodigo())
	                                     .build();
	}

}
