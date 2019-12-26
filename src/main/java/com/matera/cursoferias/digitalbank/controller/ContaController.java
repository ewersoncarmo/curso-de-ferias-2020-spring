package com.matera.cursoferias.digitalbank.controller;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.matera.cursoferias.digitalbank.controller.base.ControllerBase;
import com.matera.cursoferias.digitalbank.domain.enumerator.TipoLancamento;
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.TransferenciaRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ComprovanteResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ExtratoResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ResponseDTO;
import com.matera.cursoferias.digitalbank.exception.handler.BusinessExceptionHandler;
import com.matera.cursoferias.digitalbank.exception.handler.GenericExceptionHandler;
import com.matera.cursoferias.digitalbank.exception.handler.InvalidFormatExceptionHandler;
import com.matera.cursoferias.digitalbank.exception.handler.MethodArgumentNotValidExceptionHandler;
import com.matera.cursoferias.digitalbank.service.ContaService;

@RestController
@RequestMapping("/api/v1/contas")
public class ContaController extends ControllerBase {

    private static final Logger LOG = LoggerFactory.getLogger(ContaController.class);

	private final ContaService contaService;

	public ContaController(ContaService contaService, BusinessExceptionHandler businessExceptionHandler,
	                       MethodArgumentNotValidExceptionHandler methodArgumentNotValidExceptionHandler,
	                       InvalidFormatExceptionHandler invalidFormatExceptionHandler, GenericExceptionHandler genericExceptionHandler) {
	    super(businessExceptionHandler, methodArgumentNotValidExceptionHandler, invalidFormatExceptionHandler, genericExceptionHandler);
	    this.contaService = contaService;
	}

    @PostMapping(value = "/{id}/depositar")
	public ResponseEntity<ResponseDTO<ComprovanteResponseDTO>> efetuaDeposito(@PathVariable("id") Long id,
			                                                                  @Valid @RequestBody LancamentoRequestDTO lancamentoRequestDTO) {
        LOG.debug("Iniciando POST em /api/v1/contas/{id}/depositar com id {} e request {}", id, lancamentoRequestDTO);

		ComprovanteResponseDTO comprovanteResponseDTO = contaService.efetuaLancamento(id, lancamentoRequestDTO, TipoLancamento.DEPOSITO);

		LOG.debug("Finalizando POST em /api/v1/contas/{id}/depositar com response {}", comprovanteResponseDTO);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseDTO<>(comprovanteResponseDTO));
	}

	@PostMapping(value = "/{id}/sacar")
	public ResponseEntity<ResponseDTO<ComprovanteResponseDTO>> efetuaSaque(@PathVariable("id") Long id,
			                                                               @Valid @RequestBody LancamentoRequestDTO lancamentoRequestDTO) {
	    LOG.debug("Iniciando POST em /api/v1/contas/{id}/sacar com id {} e request {}", id, lancamentoRequestDTO);

		ComprovanteResponseDTO comprovanteResponseDTO = contaService.efetuaLancamento(id, lancamentoRequestDTO, TipoLancamento.SAQUE);

		LOG.debug("Finalizando POST em /api/v1/contas/{id}/sacar com response {}", comprovanteResponseDTO);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseDTO<>(comprovanteResponseDTO));
	}

	@PostMapping(value = "/{id}/pagar")
	public ResponseEntity<ResponseDTO<ComprovanteResponseDTO>> efetuaPagamento(@PathVariable("id") Long id,
			                                                                   @Valid @RequestBody LancamentoRequestDTO lancamentoRequestDTO) {
	    LOG.debug("Iniciando POST em /api/v1/contas/{id}/pagar com id {} e request {}", id, lancamentoRequestDTO);

		ComprovanteResponseDTO comprovanteResponseDTO = contaService.efetuaLancamento(id, lancamentoRequestDTO, TipoLancamento.PAGAMENTO);

		LOG.debug("Finalizando POST em /api/v1/contas/{id}/pagar com response {}", comprovanteResponseDTO);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseDTO<>(comprovanteResponseDTO));
	}

	@PostMapping(value = "/{id}/transferir")
	public ResponseEntity<ResponseDTO<ComprovanteResponseDTO>> efetuaTransferencia(@PathVariable("id") Long id,
			                                                                       @Valid @RequestBody TransferenciaRequestDTO transferenciaRequestDTO) {
	    LOG.debug("Iniciando POST em /api/v1/contas/{id}/transferir com id {} e request {}", id, transferenciaRequestDTO);

		ComprovanteResponseDTO comprovanteResponseDTO = contaService.efetuaTransferencia(id, transferenciaRequestDTO);

		LOG.debug("Finalizando POST em /api/v1/contas/{id}/transferir com response {}", comprovanteResponseDTO);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseDTO<>(comprovanteResponseDTO));
	}

	@GetMapping(value = "/{id}/lancamentos", params = { "!dataInicial", "!dataFinal" })
	public ResponseEntity<ResponseDTO<ExtratoResponseDTO>> consultaExtratoCompleto(@PathVariable("id") Long id) {
	    LOG.debug("Iniciando GET em /api/v1/contas/{id}/lancamentos com id {}", id);

		ExtratoResponseDTO extratoResponseDTO = contaService.consultaExtratoCompleto(id);

		LOG.debug("Finalizando GET em /api/v1/contas/{id}/lancamentos com response {}", extratoResponseDTO);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseDTO<>(extratoResponseDTO));
	}

	@GetMapping(value = "/{id}/lancamentos", params = { "dataInicial", "dataFinal" })
	public ResponseEntity<ResponseDTO<ExtratoResponseDTO>> consultaExtratoPorPeriodo(@PathVariable("id") Long id,
																				     @RequestParam(value = "dataInicial", required = true) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate dataInicial,
																				     @RequestParam(value = "dataFinal", required = true) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate dataFinal) {
	    LOG.debug("Iniciando GET em /api/v1/contas/{id}/lancamentos com id {}, dataInicial {} e dataFinal {}", id, dataInicial, dataFinal);

		ExtratoResponseDTO extratoResponseDTO = contaService.consultaExtratoPorPeriodo(id, dataInicial, dataFinal);

		LOG.debug("Finalizando GET em /api/v1/contas/{id}/lancamentos com response {}", extratoResponseDTO);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseDTO<>(extratoResponseDTO));
	}

	@GetMapping(value = "/{idConta}/lancamentos/{idLancamento}")
    public ResponseEntity<ResponseDTO<ComprovanteResponseDTO>> consultaComprovanteLancamento(@PathVariable("idConta") Long idConta,
                                                                                             @PathVariable("idLancamento") Long idLancamento) {
	    LOG.debug("Iniciando GET em /api/v1/contas/{idConta}/lancamentos/{idLancamento} com idConta {} e idLancamento {}", idConta, idLancamento);

        ComprovanteResponseDTO comprovanteResponseDTO = contaService.consultaComprovanteLancamento(idConta, idLancamento);

        LOG.debug("Finalizando GET em /api/v1/contas/{idConta}/lancamentos/{idLancamento} com response {}", comprovanteResponseDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO<>(comprovanteResponseDTO));
    }

	@DeleteMapping(value = "/{idConta}/lancamentos/{idLancamento}")
    public ResponseEntity<ResponseDTO<ComprovanteResponseDTO>> removeLancamentoEstorno(@PathVariable("idConta") Long idConta,
                                                                                       @PathVariable("idLancamento") Long idLancamento) {
        LOG.debug("Iniciando DELETE em /api/v1/contas/{idConta}/lancamentos/{idLancamento} com idConta {} e idLancamento {}", idConta, idLancamento);

        contaService.removeLancamentoEstorno(idConta, idLancamento);

        LOG.debug("Finalizando DELETE em /api/v1/contas/{idConta}/lancamentos/{idLancamento}");

        return ResponseEntity
                .noContent()
                .build();
    }

	@PostMapping(value = "/{idConta}/lancamentos/{idLancamento}/estornar")
    public ResponseEntity<ResponseDTO<ComprovanteResponseDTO>> estornaLancamento(@PathVariable("idConta") Long idConta,
                                                                                 @PathVariable("idLancamento") Long idLancamento) {
	    LOG.debug("Iniciando POST em /api/v1/contas/{idConta}/lancamentos/{idLancamento}/estornar com idConta {} e idLancamento {}", idConta, idLancamento);

        ComprovanteResponseDTO comprovanteResponseDTO = contaService.estornaLancamento(idConta, idLancamento);

        LOG.debug("Finalizando POST em /api/v1/contas/{idConta}/lancamentos/{idLancamento}/estornar com response {}", comprovanteResponseDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO<>(comprovanteResponseDTO));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<ContaResponseDTO>>> consultaTodas() {
        LOG.debug("Iniciando GET em /api/v1/contas");

        List<ContaResponseDTO> contasResponseDTO = contaService.consultaTodas();

        LOG.debug("Finalizando GET em /api/v1/contas com response size {}", contasResponseDTO.size());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO<>(contasResponseDTO));
    }

    @PostMapping(value = "/{id}/bloquear")
    public ResponseEntity<Void> bloqueia(@PathVariable("id") Long id) {
        LOG.debug("Iniciando POST em /api/v1/contas/{id}/bloquear com id {}", id);

        contaService.bloqueiaConta(id);

        LOG.debug("Finalizando POST em /api/v1/contas/{id}/bloquear");

        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping(value = "/{id}/desbloquear")
    public ResponseEntity<Void> desbloqueia(@PathVariable("id") Long id) {
        LOG.debug("Iniciando POST em /api/v1/contas/{id}/desbloquear com id {}", id);

        contaService.desbloqueiaConta(id);

        LOG.debug("Finalizando POST em /api/v1/contas/{id}/desbloquear");

        return ResponseEntity
                .noContent()
                .build();
    }

}
