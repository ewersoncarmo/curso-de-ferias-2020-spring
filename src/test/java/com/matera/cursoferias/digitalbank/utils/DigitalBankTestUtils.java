package com.matera.cursoferias.digitalbank.utils;

import static io.restassured.RestAssured.given;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.matera.cursoferias.digitalbank.domain.entity.Cliente;
import com.matera.cursoferias.digitalbank.domain.entity.Conta;
import com.matera.cursoferias.digitalbank.domain.entity.Lancamento;
import com.matera.cursoferias.digitalbank.domain.enumerator.Natureza;
import com.matera.cursoferias.digitalbank.domain.enumerator.SituacaoConta;
import com.matera.cursoferias.digitalbank.domain.enumerator.TipoLancamento;
import com.matera.cursoferias.digitalbank.dto.request.ClienteRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.TransferenciaRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ComprovanteResponseDTO;
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
			rendaMensal(BigDecimal.valueOf(10000)).
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
                                .rendaMensal(BigDecimal.valueOf(10000))
                                .logradouro("Avenida São Paulo")
                                .numero(1287)
                                .complemento("Apto 207")
                                .bairro("Centro")
                                .cidade("Maringá")
                                .estado("PR")
                                .cep("87005002")
                                .build();
    }

	public static Conta buildContaEntidade() {
	    return Conta.builder().id(2L)
                              .numeroAgencia(1234)
                              .numeroConta(102030L)
                              .saldo(BigDecimal.valueOf(5000))
                              .situacao(SituacaoConta.ABERTA.getCodigo())
                              .cliente(buildClienteEntidade())
                              .build();
	}

	public static ContaResponseDTO buildContaResponseDTO() {
	    return ContaResponseDTO.builder().idCliente(1L)
	                                     .idConta(2L)
	                                     .numeroAgencia(1234)
	                                     .numeroConta(102030L)
	                                     .saldo(BigDecimal.valueOf(5000))
	                                     .situacao(SituacaoConta.ABERTA.getCodigo())
	                                     .build();
	}

	public static LancamentoRequestDTO buildLancamentoRequestDTO(BigDecimal valor) {
        return LancamentoRequestDTO.builder().descricao("Lançamento Teste")
                                             .valor(valor)
                                             .build();
    }

	public static Lancamento buildLancamentoEntidade(TipoLancamento tipoLancamento, Natureza natureza, BigDecimal valor) {
        return Lancamento.builder().id(1L)
                                   .codigoAutenticacao("123456")
                                   .conta(buildContaEntidade())
                                   .dataHora(LocalDateTime.now())
                                   .descricao("Lançamento Teste")
                                   .natureza(natureza.getCodigo())
                                   .tipoLancamento(tipoLancamento.getCodigo())
                                   .valor(valor)
                                   .build();
    }

	public static ComprovanteResponseDTO buildComprovanteResponseDTO() {
	    return ComprovanteResponseDTO.builder().codigoAutenticacao("123456")
	                                           .dataHora(LocalDateTime.now())
	                                           .descricao("Lançamento Teste")
	                                           .idLancamento(1L)
	                                           .natureza(Natureza.CREDITO.getCodigo())
	                                           .numeroAgencia(1)
	                                           .numeroConta(12345L)
	                                           .tipoLancamento(TipoLancamento.DEPOSITO.getCodigo())
	                                           .valor(BigDecimal.valueOf(100))
	                                           .build();
	}

	public static TransferenciaRequestDTO buildTransferenciaRequestDTO(Integer numeroAgencia, Long numeroConta, BigDecimal valor) {
	    return TransferenciaRequestDTO.builder()
                                      .descricao("Transferência Teste")
                                      .numeroAgencia(numeroAgencia)
                                      .numeroConta(numeroConta)
                                      .valor(valor)
                                      .build();
	}

}
