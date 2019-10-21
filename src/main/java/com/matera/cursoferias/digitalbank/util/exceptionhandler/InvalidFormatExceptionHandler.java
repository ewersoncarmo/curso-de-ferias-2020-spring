package com.matera.cursoferias.digitalbank.util.exceptionhandler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.matera.cursoferias.digitalbank.dto.response.ErroResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ResponseDTO;

@Component
public class InvalidFormatExceptionHandler implements ExceptionHandler {

	@Override
	public ResponseEntity<ResponseDTO<Object>> handleException(Exception e) {
		List<ErroResponseDTO> erros = new ArrayList<>();
		
		List<Reference> paths = ((InvalidFormatException) e).getPath();
		for (Reference path : paths) {
			String campo = path.getFieldName();
			String mensagem = String.format("%s: %s", campo, "O valor informado � inv�lido");
			
			erros.add(new ErroResponseDTO(campo, mensagem));
		}
		
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ResponseDTO.comErros(erros));
	}

}
