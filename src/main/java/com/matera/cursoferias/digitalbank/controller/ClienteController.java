package com.matera.cursoferias.digitalbank.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.matera.cursoferias.digitalbank.controller.base.ControllerBase;
import com.matera.cursoferias.digitalbank.dto.request.ClienteRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.service.ClienteService;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController extends ControllerBase {

	@Autowired
	private ClienteService clienteService;
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<ContaResponseDTO> post(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
		ContaResponseDTO contaResponseDTO = clienteService.criarClienteEConta(clienteRequestDTO);
		
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(contaResponseDTO);
	}
}
