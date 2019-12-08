package com.matera.cursoferias.digitalbank.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.matera.cursoferias.digitalbank.dto.response.ErroResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ResponseDTO;
import com.matera.cursoferias.digitalbank.exception.BusinessException;

@Component
public class BusinessExceptionHandler implements ExceptionHandler<BusinessException> {

	@Override
	public ResponseEntity<ResponseDTO<Object>> handleException(BusinessException exception) {
		ErroResponseDTO erro = new ErroResponseDTO(exception.getMessage());
		
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ResponseDTO.comErro(erro));
	}

}
