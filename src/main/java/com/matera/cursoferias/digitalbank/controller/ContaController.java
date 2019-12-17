package com.matera.cursoferias.digitalbank.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matera.cursoferias.digitalbank.controller.base.ControllerBase;
import com.matera.cursoferias.digitalbank.domain.enumerator.Natureza;
import com.matera.cursoferias.digitalbank.domain.enumerator.TipoLancamento;
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.TransferenciaRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ComprovanteResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ExtratoResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ResponseDTO;
import com.matera.cursoferias.digitalbank.service.ContaService;

@RestController
@RequestMapping("/api/v1/contas")
public class ContaController extends ControllerBase {

	@Autowired
	private ContaService contaService;

	@PostMapping(value = "/{id}/depositar")
	public ResponseEntity<ResponseDTO<ComprovanteResponseDTO>> depositar(@PathVariable("id") Long id,
			@Valid @RequestBody LancamentoRequestDTO lancamentoRequestDTO) {
		ComprovanteResponseDTO comprovanteResponseDTO = contaService.efetuarLancamento(id, lancamentoRequestDTO, Natureza.CREDITO, TipoLancamento.DEPOSITO);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseDTO<>(comprovanteResponseDTO));
	}

	@PostMapping(value = "/{id}/sacar")
	public ResponseEntity<ResponseDTO<ComprovanteResponseDTO>> sacar(@PathVariable("id") Long id,
			@Valid @RequestBody LancamentoRequestDTO lancamentoRequestDTO) {
		ComprovanteResponseDTO comprovanteResponseDTO = contaService.efetuarLancamento(id, lancamentoRequestDTO, Natureza.DEBITO, TipoLancamento.SAQUE);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseDTO<>(comprovanteResponseDTO));
	}

	@PostMapping(value = "/{id}/pagar")
	public ResponseEntity<ResponseDTO<ComprovanteResponseDTO>> pagar(@PathVariable("id") Long id,
			@Valid @RequestBody LancamentoRequestDTO lancamentoRequestDTO) {
		ComprovanteResponseDTO comprovanteResponseDTO = contaService.efetuarLancamento(id, lancamentoRequestDTO, Natureza.DEBITO, TipoLancamento.PAGAMENTO);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseDTO<>(comprovanteResponseDTO));
	}

	@PostMapping(value = "/{id}/transferir")
	public ResponseEntity<ResponseDTO<ComprovanteResponseDTO>> transferir(@PathVariable("id") Long id,
			@Valid @RequestBody TransferenciaRequestDTO transferenciaRequestDTO) {
		ComprovanteResponseDTO comprovanteResponseDTO = contaService.efetuarTransferencia(id, transferenciaRequestDTO);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseDTO<>(comprovanteResponseDTO));
	}

	@GetMapping(value = "/{id}/lancamentos")
	public ResponseEntity<ResponseDTO<ExtratoResponseDTO>> consultarExtratoCompleto(@PathVariable("id") Long id) {
		ExtratoResponseDTO extratoResponseDTO = contaService.consultarExtratoCompleto(id);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseDTO<>(extratoResponseDTO));
	}

    @GetMapping
    public ResponseEntity<ResponseDTO<List<ContaResponseDTO>>> consultarTodas() {
        List<ContaResponseDTO> contasResponseDTO = contaService.consultarTodas();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO<>(contasResponseDTO));
    }

}
