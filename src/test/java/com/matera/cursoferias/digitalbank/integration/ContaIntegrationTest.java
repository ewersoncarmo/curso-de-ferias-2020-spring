package com.matera.cursoferias.digitalbank.integration;

import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildClienteRequestDTO;
import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildDeleteRequest;
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
import com.matera.cursoferias.digitalbank.dto.response.ComprovanteResponseDTO;
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
		BigDecimal valor = BigDecimal.valueOf(100);
		String descricao = "Depósito";
		
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK).
			root("dados").
				body("idLancamento", greaterThan(0)).
				body("codigoAutenticacao", notNullValue()).
				body("dataHora", notNullValue()).
				body("valor", equalTo(valor.intValue())).
				body("natureza", equalTo(Natureza.CREDITO.getCodigo())).
				body("tipoLancamento", equalTo(TipoLancamento.DEPOSITO.getCodigo())).
				body("descricao", equalTo(descricao));

		assertSaldoConta(contaResponse.getDados().getIdConta(), valor);
	}

	@Test
	public void efetuaDepositoContaNaoEncontradaTest() {
		efetuaLancamentoComErro("/2/depositar", "DB-3");
	}
	
	@Test
	public void efetuaDepositoContaBloqueadaTest() {
		bloqueiaConta(contaResponse.getDados().getIdConta(), HttpStatus.NO_CONTENT);
			
		efetuaLancamentoComErro("/" + contaResponse.getDados().getIdConta() + "/depositar", "DB-15");
	}

	@Test
	public void efetuaSaqueTest() {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

		LancamentoRequestDTO saque = new LancamentoRequestDTO();
		saque.setValor(BigDecimal.valueOf(50));
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

		assertSaldoConta(contaResponse.getDados().getIdCliente(), saque.getValor());
	}
	
	@Test
	public void efetuaSaqueContaNaoEncontradaTest() {
		efetuaLancamentoComErro("/2/sacar", "DB-3");
	}
	
	@Test
	public void efetuaSaqueContaBloqueadaTest() {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

		bloqueiaConta(contaResponse.getDados().getIdConta(), HttpStatus.NO_CONTENT);
		
		efetuaLancamentoComErro("/" + contaResponse.getDados().getIdConta() + "/sacar", "DB-15");
	}
	
	@Test
	public void efetuaSaqueContaSemSaldoTest() {
		efetuaLancamentoComErro("/" + contaResponse.getDados().getIdConta() + "/sacar", "DB-6");
	}

	@Test
	public void efetuaPagamentoTest() {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

		LancamentoRequestDTO lancamento = new LancamentoRequestDTO();
		lancamento.setValor(BigDecimal.valueOf(50));
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

		assertSaldoConta(contaResponse.getDados().getIdCliente(), lancamento.getValor());
	}
	
	@Test
	public void efetuaPagamentoContaNaoEncontradaTest() {
		efetuaLancamentoComErro("/2/pagar", "DB-3");
	}
	
	@Test
	public void efetuaPagamentoContaBloqueadaTest() {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

		bloqueiaConta(contaResponse.getDados().getIdConta(), HttpStatus.NO_CONTENT);
		
		efetuaLancamentoComErro("/" + contaResponse.getDados().getIdConta() + "/pagar", "DB-15");
	}
	
	@Test
	public void efetuaPagamentoContaSemSaldoTest() {
		efetuaLancamentoComErro("/" + contaResponse.getDados().getIdConta() + "/pagar", "DB-6");
	}
	
	@Test
	public void efetuaTransferenciaTest() {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

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
				valor(BigDecimal.valueOf(30)).
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

		assertSaldoConta(contaResponse.getDados().getIdCliente(), BigDecimal.valueOf(70));
		assertSaldoConta(contaDestino.getDados().getIdCliente(), BigDecimal.valueOf(30));
	}

	@Test
	public void efetuaTransferenciaContaDebitoNaoEncontradaTest() {
		efetuaTransferenciaComErro(1, 2L, "/2/transferir", "DB-3");
	}
	
	@Test
	public void efetuaTransferenciaContaCreditoNaoEncontradaTest() {
		efetuaTransferenciaComErro(1, 2L, "/" + contaResponse.getDados().getIdConta() + "/transferir", "DB-5");
	}
	
	@Test
	public void efetuaTransferenciaContaDebitoBloqueadaTest() {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

		bloqueiaConta(contaResponse.getDados().getIdConta(), HttpStatus.NO_CONTENT);
		
		ClienteRequestDTO clienteDestino = buildClienteRequestDTO();
		clienteDestino.setCpf("57573694695");
		clienteDestino.setTelefone(997242244L);

		ResponseDTO<ContaResponseDTO> contaDestino = buildPostRequest(clienteDestino, "digitalbank/api/v1/clientes", HttpStatus.CREATED).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ContaResponseDTO>>() {});

		efetuaTransferenciaComErro(contaDestino.getDados().getNumeroAgencia(), contaDestino.getDados().getNumeroConta(), "/" + contaResponse.getDados().getIdConta() + "/transferir", "DB-15");
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

		efetuaTransferenciaComErro(contaDestino.getDados().getNumeroAgencia(), contaDestino.getDados().getNumeroConta(), "/" + contaResponse.getDados().getIdConta() + "/transferir", "DB-6");
	}
	
	@Test
	public void efetuaTransferenciaContaCreditoBloqueadaTest() {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

		ClienteRequestDTO clienteDestino = buildClienteRequestDTO();
		clienteDestino.setCpf("57573694695");
		clienteDestino.setTelefone(997242244L);

		ResponseDTO<ContaResponseDTO> contaDestino = buildPostRequest(clienteDestino, "digitalbank/api/v1/clientes", HttpStatus.CREATED).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ContaResponseDTO>>() {});

		bloqueiaConta(contaDestino.getDados().getIdConta(), HttpStatus.NO_CONTENT);

		efetuaTransferenciaComErro(contaDestino.getDados().getNumeroAgencia(), contaDestino.getDados().getNumeroConta(), "/" + contaResponse.getDados().getIdConta() + "/transferir", "DB-15");
	}
	
	@Test
	public void bloqueiaContaTest() {
		bloqueiaConta(contaResponse.getDados().getIdConta(), HttpStatus.NO_CONTENT);
		
		assertSituacaoConta(contaResponse.getDados().getIdConta(), SituacaoConta.BLOQUEADA.getCodigo());
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
		
		assertSituacaoConta(contaResponse.getDados().getIdConta(), SituacaoConta.ABERTA.getCodigo());
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
	
	@Test
	public void estornaLancamentoDepositoTest() {
		ResponseDTO<ComprovanteResponseDTO> comprovante = efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});

		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovante.getDados().getIdLancamento()).
			build();
		
		buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.OK).
			root("dados").
				body("idLancamento", greaterThan(0)).
				body("codigoAutenticacao", notNullValue()).
				body("dataHora", notNullValue()).
				body("valor", equalTo(comprovante.getDados().getValor().floatValue())).
				body("natureza", equalTo(Natureza.DEBITO.getCodigo())).
				body("tipoLancamento", equalTo(TipoLancamento.ESTORNO.getCodigo())).
				body("descricao", equalTo("Estorno do lançamento " + comprovante.getDados().getIdLancamento()));
		
		assertSaldoConta(contaResponse.getDados().getIdConta(), BigDecimal.ZERO);
	}
	
	@Test
	public void estornaLancamentoDepositoEstornoTest() {
		ResponseDTO<ComprovanteResponseDTO> comprovante = efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});

		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovante.getDados().getIdLancamento()).
			build();
		
		ResponseDTO<ComprovanteResponseDTO> comprovanteEstorno = buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});
		
		RequestSpecification requestSpecificationEstorno = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovanteEstorno.getDados().getIdLancamento()).
			build();
		
		buildPostRequest(requestSpecificationEstorno, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-8"));
	}
	
	@Test
	public void estornaLancamentoDepositoJaEstornadoTest() {
		ResponseDTO<ComprovanteResponseDTO> comprovante = efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});

		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovante.getDados().getIdLancamento()).
			build();
		
		buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.OK);
		
		buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-9"));
	}
	
	@Test
	public void estornaLancamentoDepositoContaBloqueadaTest() {
		ResponseDTO<ComprovanteResponseDTO> comprovante = efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});

		bloqueiaConta(contaResponse.getDados().getIdConta(), HttpStatus.NO_CONTENT);
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovante.getDados().getIdLancamento()).
			build();
		
		buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-15"));
	}
	
	@Test
	public void estornaLancamentoDepositoContaSemSaldoTest() {
		ResponseDTO<ComprovanteResponseDTO> comprovante = efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});

		LancamentoRequestDTO lancamento = new LancamentoRequestDTO();
		lancamento.setValor(BigDecimal.valueOf(100));
		lancamento.setDescricao("Pagamento");

		buildPostRequest(lancamento, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/pagar", HttpStatus.OK);
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovante.getDados().getIdLancamento()).
			build();
		
		buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-11"));
	}
	
	@Test
	public void estornaLancamentoSaqueTest() {
		estornaLancamentoDebitoTest("/sacar");
	}
	
	@Test
	public void estornaLancamentoSaqueEstornoTest() {
		estornaLancamentoDebitoEstornoTest("/sacar");
	}
	
	@Test
	public void estornaLancamentoSaqueJaEstornadoTest() {
		estornaLancamentoDebitoJaEstornadoTest("/sacar");
	}
	
	@Test
	public void estornaLancamentoSaqueContaBloqueadaTest() {
		estornaLancamentoDebitoContaBloqueadaTest("/sacar");
	}
	
	@Test
	public void estornaLancamentoPagamentoTest() {
		estornaLancamentoDebitoTest("/pagar");
	}
	
	@Test
	public void estornaLancamentoPagamentoEstornoTest() {
		estornaLancamentoDebitoEstornoTest("/pagar");
	}
	
	@Test
	public void estornaLancamentoPagamentoJaEstornadoTest() {
		estornaLancamentoDebitoJaEstornadoTest("/pagar");
	}
	
	@Test
	public void estornaLancamentoPagamentoContaBloqueadaTest() {
		estornaLancamentoDebitoContaBloqueadaTest("/pagar");
	}
	
	@Test
	public void estornaLancamentoNaoEncontradoTest() {
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", 1).
			build();
		
		buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-7"));
	}
	
	@Test
	public void estornaLancamentoTransferenciaTest() {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

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
				valor(BigDecimal.valueOf(30)).
				descricao("Transferência").
			build();
		
		buildPostRequest(transferencia, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/transferir", HttpStatus.OK);
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaDestino.getDados().getIdConta()).
			addPathParam("idLancamento", 3).
			build();
		
		buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.OK).
			root("dados").
				body("idLancamento", greaterThan(0)).
				body("codigoAutenticacao", notNullValue()).
				body("dataHora", notNullValue()).
				body("valor", equalTo(transferencia.getValor().floatValue())).
				body("natureza", equalTo(Natureza.DEBITO.getCodigo())).
				body("tipoLancamento", equalTo(TipoLancamento.ESTORNO.getCodigo())).
				body("descricao", equalTo("Estorno do lançamento " + 3));
		
		assertSaldoConta(contaResponse.getDados().getIdCliente(), BigDecimal.valueOf(100));
		assertSaldoConta(contaDestino.getDados().getIdCliente(), BigDecimal.ZERO);
	}
	
	@Test
	public void estornaLancamentoTransferenciaEstornoTest() {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

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
				valor(BigDecimal.valueOf(30)).
				descricao("Transferência").
			build();
		
		buildPostRequest(transferencia, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/transferir", HttpStatus.OK);
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaDestino.getDados().getIdConta()).
			addPathParam("idLancamento", 3).
			build();
		
		buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.OK);

		RequestSpecification requestSpecificationEstorno = new RequestSpecBuilder().
			addPathParam("idConta", contaDestino.getDados().getIdConta()).
			addPathParam("idLancamento", 5).
			build();
		
		buildPostRequest(requestSpecificationEstorno, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-8"));
	}
	
	@Test
	public void estornaLancamentoTransferenciaJaEstornadoTest() {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

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
				valor(BigDecimal.valueOf(30)).
				descricao("Transferência").
			build();
		
		buildPostRequest(transferencia, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/transferir", HttpStatus.OK);
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaDestino.getDados().getIdConta()).
			addPathParam("idLancamento", 3).
			build();
		
		buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.OK);
		
		buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-9"));
	}
	
	@Test
	public void estornaLancamentoTransferenciaContaDebitadaTest() {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

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
				valor(BigDecimal.valueOf(30)).
				descricao("Transferência").
			build();
		
		ResponseDTO<ComprovanteResponseDTO> comprovante = buildPostRequest(transferencia, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/transferir", HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovante.getDados().getIdLancamento()).
			build();
		
		buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-10"));
	}
	
	@Test
	public void estornaLancamentoTransferenciaContaBloqueadaTest() {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

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
				valor(BigDecimal.valueOf(30)).
				descricao("Transferência").
			build();
		
		buildPostRequest(transferencia, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/transferir", HttpStatus.OK);
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaDestino.getDados().getIdConta()).
			addPathParam("idLancamento", 3).
			build();
		
		bloqueiaConta(contaDestino.getDados().getIdConta(), HttpStatus.NO_CONTENT);
		
		buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-15"));
	}
	
	@Test
	public void estornaLancamentoTransferenciaContaSemSaldoTest() {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

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
				valor(BigDecimal.valueOf(30)).
				descricao("Transferência").
			build();
		
		buildPostRequest(transferencia, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/transferir", HttpStatus.OK);
		
		LancamentoRequestDTO lancamento = new LancamentoRequestDTO();
		lancamento.setValor(BigDecimal.valueOf(30));
		lancamento.setDescricao("Pagamento");
		
		buildPostRequest(lancamento, URL_BASE + "/" + contaDestino.getDados().getIdConta() + "/pagar", HttpStatus.OK);
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaDestino.getDados().getIdConta()).
			addPathParam("idLancamento", 3).
			build();
		
		buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-11"));
	}
	
	@Test
	public void removeLancamentoEstornoDepositoTest() {
		ResponseDTO<ComprovanteResponseDTO> comprovante = efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});

		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovante.getDados().getIdLancamento()).
			build();
		
		ResponseDTO<ComprovanteResponseDTO> comprovanteEstorno = buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});
		
		RequestSpecification requestSpecificationEstorno = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovanteEstorno.getDados().getIdLancamento()).
			build();
		
		buildDeleteRequest(requestSpecificationEstorno, URL_BASE + "/{idConta}/lancamentos/{idLancamento}", HttpStatus.NO_CONTENT);
		
		buildGetRequest(requestSpecificationEstorno, URL_BASE + "/{idConta}/lancamentos/{idLancamento}", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-7"));
	}
	
	@Test
	public void removeLancamentoOriginalDepositoTest() {
		ResponseDTO<ComprovanteResponseDTO> comprovante = efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});

		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovante.getDados().getIdLancamento()).
			build();
		
		buildDeleteRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-16"));
	}
	
	@Test
	public void removeLancamentoEstornoSaqueTest() {
		removeLancamentoDebito("/sacar");
	}
	
	@Test
	public void removeLancamentoOriginalSaqueTest() {
		removeLancamentoOriginalDebito("/sacar");
	}
	
	@Test
	public void removeLancamentoEstornoPagamentoTest() {
		removeLancamentoDebito("/pagar");
	}
	
	@Test
	public void removeLancamentoOriginalPagamentoTest() {
		removeLancamentoOriginalDebito("/pagar");
	}
	
	@Test 
	public void removeLancamentoEstornoTransferenciaTest() {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

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
				valor(BigDecimal.valueOf(30)).
				descricao("Transferência").
			build();
		
		buildPostRequest(transferencia, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/transferir", HttpStatus.OK);
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaDestino.getDados().getIdConta()).
			addPathParam("idLancamento", 3).
			build();
		
		ResponseDTO<ComprovanteResponseDTO> comprovanteEstorno = buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});
			
		RequestSpecification requestSpecificationEstorno = new RequestSpecBuilder().
			addPathParam("idConta", contaDestino.getDados().getIdConta()).
			addPathParam("idLancamento", comprovanteEstorno.getDados().getIdLancamento()).
			build();
		
		buildDeleteRequest(requestSpecificationEstorno, URL_BASE + "/{idConta}/lancamentos/{idLancamento}", HttpStatus.NO_CONTENT);
		
		buildGetRequest(requestSpecificationEstorno, URL_BASE + "/{idConta}/lancamentos/{idLancamento}", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-7"));
	}
	
	@Test 
	public void removeLancamentoOriginalTransferenciaTest() {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

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
				valor(BigDecimal.valueOf(30)).
				descricao("Transferência").
			build();
		
		buildPostRequest(transferencia, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/transferir", HttpStatus.OK);
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaDestino.getDados().getIdConta()).
			addPathParam("idLancamento", 3).
			build();
		
		buildDeleteRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-16"));
	}
	
	@Test
	public void consultaComprovanteDepositoTest() {
		ResponseDTO<ComprovanteResponseDTO> comprovante = efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});

		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovante.getDados().getIdLancamento()).
			build();
		
		buildGetRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}", HttpStatus.OK).
			root("dados").
				body("idLancamento", equalTo(comprovante.getDados().getIdLancamento().intValue())).
				body("codigoAutenticacao", notNullValue()).
				body("dataHora", notNullValue()).
				body("valor", equalTo(comprovante.getDados().getValor().floatValue())).
				body("natureza", equalTo(Natureza.CREDITO.getCodigo())).
				body("tipoLancamento", equalTo(TipoLancamento.DEPOSITO.getCodigo())).
				body("descricao", equalTo(comprovante.getDados().getDescricao()));
	}
	
	@Test
	public void consultaComprovanteSaqueTest() {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

		LancamentoRequestDTO saque = new LancamentoRequestDTO();
		saque.setValor(BigDecimal.valueOf(50));
		saque.setDescricao("Saque");

		ResponseDTO<ComprovanteResponseDTO> comprovante = buildPostRequest(saque, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/sacar", HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovante.getDados().getIdLancamento()).
			build();
		
		buildGetRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}", HttpStatus.OK).
			root("dados").
				body("idLancamento", equalTo(comprovante.getDados().getIdLancamento().intValue())).
				body("codigoAutenticacao", notNullValue()).
				body("dataHora", notNullValue()).
				body("valor", equalTo(comprovante.getDados().getValor().floatValue())).
				body("natureza", equalTo(Natureza.DEBITO.getCodigo())).
				body("tipoLancamento", equalTo(TipoLancamento.SAQUE.getCodigo())).
				body("descricao", equalTo(comprovante.getDados().getDescricao()));
	}
	
	@Test
	public void consultaComprovantePagamentoTest() {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

		LancamentoRequestDTO saque = new LancamentoRequestDTO();
		saque.setValor(BigDecimal.valueOf(50));
		saque.setDescricao("Pagamento");

		ResponseDTO<ComprovanteResponseDTO> comprovante = buildPostRequest(saque, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/pagar", HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovante.getDados().getIdLancamento()).
			build();
		
		buildGetRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}", HttpStatus.OK).
			root("dados").
				body("idLancamento", equalTo(comprovante.getDados().getIdLancamento().intValue())).
				body("codigoAutenticacao", notNullValue()).
				body("dataHora", notNullValue()).
				body("valor", equalTo(comprovante.getDados().getValor().floatValue())).
				body("natureza", equalTo(Natureza.DEBITO.getCodigo())).
				body("tipoLancamento", equalTo(TipoLancamento.PAGAMENTO.getCodigo())).
				body("descricao", equalTo(comprovante.getDados().getDescricao()));
	}
	
	@Test
	public void consultaExtratoCompleto() {
		efetuaDeposito(BigDecimal.valueOf(200), "Depósito", HttpStatus.OK);

		LancamentoRequestDTO saque = new LancamentoRequestDTO();
		saque.setValor(BigDecimal.valueOf(50));
		saque.setDescricao("Saque");

		buildPostRequest(saque, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/sacar", HttpStatus.OK);
		
		LancamentoRequestDTO pagamento = new LancamentoRequestDTO();
		pagamento.setValor(BigDecimal.valueOf(50));
		pagamento.setDescricao("Pagamento");

		buildPostRequest(pagamento, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/pagar", HttpStatus.OK);
		
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
				valor(BigDecimal.valueOf(30)).
				descricao("Transferência").
			build();
		
		buildPostRequest(transferencia, URL_BASE + "/" + contaResponse.getDados().getIdConta() + "/transferir", HttpStatus.OK);
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("id", contaResponse.getDados().getIdConta()).
			build();
		
		buildGetRequest(requestSpecification, URL_BASE + "/{id}/lancamentos", HttpStatus.OK).
			root("dados").
				body("conta.idConta", equalTo(contaResponse.getDados().getIdConta().intValue())).
				body("lancamentos", hasSize(4));
	}
	
	@Test
	public void consultaTodasContas() {
		ClienteRequestDTO clienteDestino = buildClienteRequestDTO();
		clienteDestino.setCpf("57573694695");
		clienteDestino.setTelefone(997242244L);

		buildPostRequest(clienteDestino, "digitalbank/api/v1/clientes", HttpStatus.CREATED);
		
		buildGetRequest(URL_BASE, HttpStatus.OK).
			body("dados", hasSize(2));
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
	
	private void efetuaLancamentoComErro(String url, String codigoErro) {
		LancamentoRequestDTO lancamento = new LancamentoRequestDTO();
		lancamento.setValor(BigDecimal.valueOf(50));
		lancamento.setDescricao("Lançamento");

		buildPostRequest(lancamento, URL_BASE + url, HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString(codigoErro));
	}
	
	private void efetuaTransferenciaComErro(Integer numeroAgencia, Long numeroConta, String url, String codigoErro) {
		TransferenciaRequestDTO transferencia = TransferenciaRequestDTO.
			builder().
				numeroAgencia(numeroAgencia).
				numeroConta(numeroConta).
				valor(BigDecimal.valueOf(30)).
				descricao("Transferência").
			build();
		
		buildPostRequest(transferencia, URL_BASE + url, HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString(codigoErro));
	}
	
	private void removeLancamentoDebito(String uri) {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

		LancamentoRequestDTO saque = new LancamentoRequestDTO();
		saque.setValor(BigDecimal.valueOf(50));
		saque.setDescricao("Saque");

		ResponseDTO<ComprovanteResponseDTO> comprovante = buildPostRequest(saque, URL_BASE + "/" + contaResponse.getDados().getIdConta() + uri, HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovante.getDados().getIdLancamento()).
			build();
		
		ResponseDTO<ComprovanteResponseDTO> comprovanteEstorno = buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});
		
		RequestSpecification requestSpecificationEstorno = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovanteEstorno.getDados().getIdLancamento()).
			build();
		
		buildDeleteRequest(requestSpecificationEstorno, URL_BASE + "/{idConta}/lancamentos/{idLancamento}", HttpStatus.NO_CONTENT);
		
		buildGetRequest(requestSpecificationEstorno, URL_BASE + "/{idConta}/lancamentos/{idLancamento}", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-7"));
	}
	
	private void removeLancamentoOriginalDebito(String uri) {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

		LancamentoRequestDTO saque = new LancamentoRequestDTO();
		saque.setValor(BigDecimal.valueOf(50));
		saque.setDescricao("Saque");

		ResponseDTO<ComprovanteResponseDTO> comprovante = buildPostRequest(saque, URL_BASE + "/" + contaResponse.getDados().getIdConta() + uri, HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovante.getDados().getIdLancamento()).
			build();
		
		buildDeleteRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-16"));
	}
	
	private void estornaLancamentoDebitoTest(String uri) {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);
		
		LancamentoRequestDTO lancamento = new LancamentoRequestDTO();
		lancamento.setValor(BigDecimal.valueOf(100));
		lancamento.setDescricao("Saque");

		ResponseDTO<ComprovanteResponseDTO> comprovante = buildPostRequest(lancamento, URL_BASE + "/" + contaResponse.getDados().getIdConta() + uri, HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovante.getDados().getIdLancamento()).
			build();
		
		buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.OK).
			root("dados").
				body("idLancamento", greaterThan(0)).
				body("codigoAutenticacao", notNullValue()).
				body("dataHora", notNullValue()).
				body("valor", equalTo(comprovante.getDados().getValor().floatValue())).
				body("natureza", equalTo(Natureza.CREDITO.getCodigo())).
				body("tipoLancamento", equalTo(TipoLancamento.ESTORNO.getCodigo())).
				body("descricao", equalTo("Estorno do lançamento " + comprovante.getDados().getIdLancamento()));
		
		assertSaldoConta(contaResponse.getDados().getIdConta(), BigDecimal.valueOf(100));
	}
	
	private void estornaLancamentoDebitoEstornoTest(String uri) {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

		LancamentoRequestDTO lancamento = new LancamentoRequestDTO();
		lancamento.setValor(BigDecimal.valueOf(100));
		lancamento.setDescricao("Saque");

		ResponseDTO<ComprovanteResponseDTO> comprovante = buildPostRequest(lancamento, URL_BASE + "/" + contaResponse.getDados().getIdConta() + uri, HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovante.getDados().getIdLancamento()).
			build();
		
		ResponseDTO<ComprovanteResponseDTO> comprovanteEstorno = buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});
		
		RequestSpecification requestSpecificationEstorno = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovanteEstorno.getDados().getIdLancamento()).
			build();
		
		buildPostRequest(requestSpecificationEstorno, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-8"));
	}
	
	private void estornaLancamentoDebitoJaEstornadoTest(String uri) {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

		LancamentoRequestDTO lancamento = new LancamentoRequestDTO();
		lancamento.setValor(BigDecimal.valueOf(100));
		lancamento.setDescricao("Saque");

		ResponseDTO<ComprovanteResponseDTO> comprovante = buildPostRequest(lancamento, URL_BASE + "/" + contaResponse.getDados().getIdConta() + uri, HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovante.getDados().getIdLancamento()).
			build();
		
		buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.OK);
		
		buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-9"));
	}
	
	private void estornaLancamentoDebitoContaBloqueadaTest(String uri) {
		efetuaDeposito(BigDecimal.valueOf(100), "Depósito", HttpStatus.OK);

		LancamentoRequestDTO lancamento = new LancamentoRequestDTO();
		lancamento.setValor(BigDecimal.valueOf(100));
		lancamento.setDescricao("Saque");

		ResponseDTO<ComprovanteResponseDTO> comprovante = buildPostRequest(lancamento, URL_BASE + "/" + contaResponse.getDados().getIdConta() + uri, HttpStatus.OK).
			extract().
				body().
					as(new TypeRef<ResponseDTO<ComprovanteResponseDTO>>() {});
		
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("idConta", contaResponse.getDados().getIdConta()).
			addPathParam("idLancamento", comprovante.getDados().getIdLancamento()).
			build();
		
		bloqueiaConta(contaResponse.getDados().getIdConta(), HttpStatus.NO_CONTENT);
		
		buildPostRequest(requestSpecification, URL_BASE + "/{idConta}/lancamentos/{idLancamento}/estornar", HttpStatus.BAD_REQUEST).
			body("erros", hasSize(1)).
			body("erros[0].mensagem", containsString("DB-15"));
	}

	private ValidatableResponse consultaConta(Long idConta) {
		RequestSpecification requestSpecification = new RequestSpecBuilder().
			addPathParam("id", idConta).
			build();
	
		return buildGetRequest(requestSpecification, "digitalbank/api/v1/clientes/{id}/conta", HttpStatus.OK);
	}
	
	private void assertSaldoConta(Long idConta, BigDecimal valor) {
		consultaConta(idConta).
			root("dados").
				body("saldo", equalTo(valor.floatValue()));
	}
	
	private void assertSituacaoConta(Long idConta, String situacao) {
		consultaConta(idConta).
			root("dados").
				body("situacao", equalTo(situacao));
	}
}
