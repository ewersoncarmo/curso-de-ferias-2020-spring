package com.matera.cursoferias.digitalbank.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.matera.cursoferias.digitalbank.controller.base.ControllerBase;
import com.matera.cursoferias.digitalbank.domain.enumerator.Natureza;
import com.matera.cursoferias.digitalbank.domain.enumerator.TipoLancamento;
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.TransferenicaRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ComprovanteResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ExtratoResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ResponseDTO;
import com.matera.cursoferias.digitalbank.service.ContaService;

@RestController
@RequestMapping("/api/v1/contas")
public class ContaController extends ControllerBase {

	@Autowired
	private ContaService contaService;
	
	@RequestMapping(value = "/{id}/depositar", method = RequestMethod.POST)
	public ResponseEntity<ResponseDTO<ComprovanteResponseDTO>> depositar(@PathVariable("id") Long id,
			@Valid @RequestBody LancamentoRequestDTO lancamentoRequestDTO) {
		ComprovanteResponseDTO comprovanteResponseDTO = contaService.efetuarLancamento(id, lancamentoRequestDTO, Natureza.CREDITO, TipoLancamento.DEPOSITO);
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseDTO<ComprovanteResponseDTO>(comprovanteResponseDTO));
	}
	
	@RequestMapping(value = "/{id}/sacar", method = RequestMethod.POST)
	public ResponseEntity<ResponseDTO<ComprovanteResponseDTO>> sacar(@PathVariable("id") Long id,
			@Valid @RequestBody LancamentoRequestDTO lancamentoRequestDTO) {
		ComprovanteResponseDTO comprovanteResponseDTO = contaService.efetuarLancamento(id, lancamentoRequestDTO, Natureza.DEBITO, TipoLancamento.SAQUE);
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseDTO<ComprovanteResponseDTO>(comprovanteResponseDTO));
	}
	
	@RequestMapping(value = "/{id}/pagar", method = RequestMethod.POST)
	public ResponseEntity<ResponseDTO<ComprovanteResponseDTO>> pagar(@PathVariable("id") Long id,
			@Valid @RequestBody LancamentoRequestDTO lancamentoRequestDTO) {
		ComprovanteResponseDTO comprovanteResponseDTO = contaService.efetuarLancamento(id, lancamentoRequestDTO, Natureza.DEBITO, TipoLancamento.PAGAMENTO);
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseDTO<ComprovanteResponseDTO>(comprovanteResponseDTO));
	}

	@RequestMapping(value = "/{id}/transferir", method = RequestMethod.POST)
	public ResponseEntity<ResponseDTO<ComprovanteResponseDTO>> transferir(@PathVariable("id") Long id,
			@Valid @RequestBody TransferenicaRequestDTO transferenciaRequestDTO) {
		ComprovanteResponseDTO comprovanteResponseDTO = contaService.efetuarTransferencia(id, transferenciaRequestDTO);
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseDTO<ComprovanteResponseDTO>(comprovanteResponseDTO));
	}
	
	@RequestMapping(value = "/{id}/lancamentos", method = RequestMethod.GET)
	public ResponseEntity<ResponseDTO<ExtratoResponseDTO>> consultarExtratoCompleto(@PathVariable("id") Long id) {
		ExtratoResponseDTO extratoResponseDTO = contaService.consultarExtratoCompleto(id);
		
		return ResponseEntity
				.status(HttpStatus.OK) 
				.body(new ResponseDTO<ExtratoResponseDTO>(extratoResponseDTO));
	}
}
