package com.matera.cursoferias.digitalbank.exception.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.matera.cursoferias.digitalbank.dto.response.ErroResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ResponseDTO;

@Component
public class MethodArgumentNotValidExceptionHandler implements ExceptionHandler<MethodArgumentNotValidException> {

	@Override
	public ResponseEntity<ResponseDTO<Object>> handleException(MethodArgumentNotValidException exception) {
		List<ErroResponseDTO> erros = new ArrayList<>();

		BindingResult bindingResult = exception.getBindingResult();
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			String campo = fieldError.getField();
			String mensagem = String.format("%s: %s", campo, fieldError.getDefaultMessage());
			
			erros.add(new ErroResponseDTO(campo, mensagem));
		}
		
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ResponseDTO.comErros(erros));
	}
}
