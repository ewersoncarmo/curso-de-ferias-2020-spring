package com.matera.cursoferias.digitalbank.util.exceptionhandler;

import org.springframework.http.ResponseEntity;

import com.matera.cursoferias.digitalbank.dto.response.ResponseDTO;

public interface ExceptionHandler<T> {

	public ResponseEntity<ResponseDTO<Object>> handleException(T exception);
}
