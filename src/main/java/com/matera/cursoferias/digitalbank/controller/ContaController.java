package com.matera.cursoferias.digitalbank.controller;

import java.util.List;

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
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.TransferenicaRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.LancamentoResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ResponseDTO;
import com.matera.cursoferias.digitalbank.service.ContaService;

@RestController
@RequestMapping("/api/v1/contas")
public class ContaController extends ControllerBase {

	@Autowired
	private ContaService contaService;
	
	@RequestMapping(value = "/{id}/efetuarlancamento", method = RequestMethod.POST)
	public ResponseEntity<ResponseDTO<ContaResponseDTO>> efetuarLancamento(@PathVariable("id") Long id,
			@Valid @RequestBody LancamentoRequestDTO lancamentoRequestDTO) {
		ContaResponseDTO contaResponseDTO = contaService.efetuarLancamento(id, lancamentoRequestDTO);
		
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(new ResponseDTO<ContaResponseDTO>(contaResponseDTO));
	}
	
	@RequestMapping(value = "/{id}/efetuartransferencia", method = RequestMethod.POST)
	public ResponseEntity<ResponseDTO<ContaResponseDTO>> efetuarTransferencia(@PathVariable("id") Long id,
			@Valid @RequestBody TransferenicaRequestDTO transferenciaRequestDTO) {
		ContaResponseDTO contaResponseDTO = contaService.efetuarTransferencia(id, transferenciaRequestDTO);
		
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(new ResponseDTO<ContaResponseDTO>(contaResponseDTO));
	}
	
	@RequestMapping(value = "/{id}/consultarextratocompleto", method = RequestMethod.GET)
	public ResponseEntity<ResponseDTO<List<LancamentoResponseDTO>>> consultarextratocompleto(@PathVariable("id") Long id) {
		List<LancamentoResponseDTO> lancamentosResponseDTO = contaService.consultarextratocompleto(id);
		
		return ResponseEntity
				.status(HttpStatus.OK) 
				.body(new ResponseDTO<List<LancamentoResponseDTO>>(lancamentosResponseDTO));
	}
}
