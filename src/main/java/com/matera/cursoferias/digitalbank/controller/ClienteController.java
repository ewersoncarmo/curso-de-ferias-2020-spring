package com.matera.cursoferias.digitalbank.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matera.cursoferias.digitalbank.controller.base.ControllerBase;
import com.matera.cursoferias.digitalbank.dto.request.ClienteRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ClienteResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ResponseDTO;
import com.matera.cursoferias.digitalbank.service.ClienteService;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController extends ControllerBase {

	@Autowired
	private ClienteService clienteService;

	@PostMapping
	public ResponseEntity<ResponseDTO<ContaResponseDTO>> cadastrar(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
		ContaResponseDTO contaResponseDTO = clienteService.cadastrar(clienteRequestDTO);

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(new ResponseDTO<>(contaResponseDTO));
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<ResponseDTO<ClienteResponseDTO>> consultar(@PathVariable("id") Long id) {
		ClienteResponseDTO clienteResponseDTO = clienteService.consultar(id);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseDTO<>(clienteResponseDTO));
	}

	@GetMapping
    public ResponseEntity<ResponseDTO<List<ClienteResponseDTO>>> consultarTodos() {
        List<ClienteResponseDTO> clientesResponseDTO = clienteService.consultarTodos();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO<>(clientesResponseDTO));
    }

	@PutMapping(value = "/{id}")
	public ResponseEntity<Void> atualizar(@PathVariable("id") Long id,
			@Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
		clienteService.atualizar(id, clienteRequestDTO);

		return ResponseEntity
				.noContent()
				.build();
	}
}
