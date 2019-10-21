package com.matera.cursoferias.digitalbank.util.exceptionhandler;

import org.springframework.http.ResponseEntity;

import com.matera.cursoferias.digitalbank.dto.response.ResponseDTO;

public interface ExceptionHandler {

	public ResponseEntity<ResponseDTO<Object>> handleException(Exception e);
}
