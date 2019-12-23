package com.matera.cursoferias.digitalbank.controller;

import java.util.List;

import javax.validation.Valid;

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
import com.matera.cursoferias.digitalbank.exception.handler.BusinessExceptionHandler;
import com.matera.cursoferias.digitalbank.exception.handler.GenericExceptionHandler;
import com.matera.cursoferias.digitalbank.exception.handler.InvalidFormatExceptionHandler;
import com.matera.cursoferias.digitalbank.exception.handler.MethodArgumentNotValidExceptionHandler;
import com.matera.cursoferias.digitalbank.service.ClienteService;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController extends ControllerBase {

	private final ClienteService clienteService;

	public ClienteController(ClienteService clienteService, BusinessExceptionHandler businessExceptionHandler,
	                         MethodArgumentNotValidExceptionHandler methodArgumentNotValidExceptionHandler,
	                         InvalidFormatExceptionHandler invalidFormatExceptionHandler, GenericExceptionHandler genericExceptionHandler) {
	    super(businessExceptionHandler, methodArgumentNotValidExceptionHandler, invalidFormatExceptionHandler, genericExceptionHandler);
	    this.clienteService = clienteService;
    }

    @PostMapping
	public ResponseEntity<ResponseDTO<ContaResponseDTO>> cadastra(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
		ContaResponseDTO contaResponseDTO = clienteService.cadastra(clienteRequestDTO);

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(new ResponseDTO<>(contaResponseDTO));
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<ResponseDTO<ClienteResponseDTO>> consultaPorId(@PathVariable("id") Long id) {
		ClienteResponseDTO clienteResponseDTO = clienteService.consultaPorId(id);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new ResponseDTO<>(clienteResponseDTO));
	}

	@GetMapping(value = "/{id}/conta")
    public ResponseEntity<ResponseDTO<ContaResponseDTO>> consultaContaPorIdCliente(@PathVariable("id") Long id) {
	    ContaResponseDTO contaResponseDTO = clienteService.consultaContaPorIdCliente(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO<>(contaResponseDTO));
    }

	@GetMapping
    public ResponseEntity<ResponseDTO<List<ClienteResponseDTO>>> consultaTodos() {
        List<ClienteResponseDTO> clientesResponseDTO = clienteService.consultaTodos();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO<>(clientesResponseDTO));
    }

	@PutMapping(value = "/{id}")
	public ResponseEntity<Void> atualiza(@PathVariable("id") Long id, @Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
		clienteService.atualiza(id, clienteRequestDTO);

		return ResponseEntity
				.noContent()
				.build();
	}

}
