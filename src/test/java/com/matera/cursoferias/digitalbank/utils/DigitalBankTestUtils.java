package com.matera.cursoferias.digitalbank.utils;

import static io.restassured.RestAssured.given;

import org.springframework.http.HttpStatus;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

public class DigitalBankTestUtils {

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
}
