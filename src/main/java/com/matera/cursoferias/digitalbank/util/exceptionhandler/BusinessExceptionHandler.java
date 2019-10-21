package com.matera.cursoferias.digitalbank.util.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.matera.cursoferias.digitalbank.dto.response.ErroResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ResponseDTO;
import com.matera.cursoferias.digitalbank.util.exceptions.BusinessException;

@Component
public class BusinessExceptionHandler implements ExceptionHandler {

	@Override
	public ResponseEntity<ResponseDTO<Object>> handleException(Exception e) {
		ErroResponseDTO erro = new ErroResponseDTO(((BusinessException) e).getMessage());
		
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ResponseDTO.comErro(erro));
	}

}
