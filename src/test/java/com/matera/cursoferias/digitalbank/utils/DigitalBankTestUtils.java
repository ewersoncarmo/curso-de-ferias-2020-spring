package com.matera.cursoferias.digitalbank.utils;

import static io.restassured.RestAssured.given;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;

import com.matera.cursoferias.digitalbank.dto.request.ClienteRequestDTO;

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
	
	public static ValidatableResponse buildPostRequest(RequestSpecification requestSpecification, String url, HttpStatus httpStatus) {
		return given().
					spec(requestSpecification).
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
	
	public static ValidatableResponse buildDeleteRequest(RequestSpecification requestSpecification, String url, HttpStatus httpStatus) {
		return given().
					spec(requestSpecification).
					log().
						all().
			   when().
			   		delete(url).
			   then().
			   		statusCode(httpStatus.value());
	}
	
	public static ValidatableResponse buildGetRequest(RequestSpecification requestSpecification, String url, HttpStatus httpStatus) {
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
}
