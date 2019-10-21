package com.matera.cursoferias.digitalbank.controller.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.matera.cursoferias.digitalbank.dto.response.ResponseDTO;
import com.matera.cursoferias.digitalbank.util.exceptionhandler.BusinessExceptionHandler;
import com.matera.cursoferias.digitalbank.util.exceptionhandler.InvalidFormatExceptionHandler;
import com.matera.cursoferias.digitalbank.util.exceptionhandler.MethodArgumentNotValidExceptionHandler;
import com.matera.cursoferias.digitalbank.util.exceptions.BusinessException;

public class ControllerBase {

	@Autowired
	private BusinessExceptionHandler businessExceptionHandler;
	
	@Autowired
	private MethodArgumentNotValidExceptionHandler methodArgumentNotValidExceptionHandler;
	
	@Autowired
	private InvalidFormatExceptionHandler invalidFormatExceptionHandler;
	
	@ExceptionHandler({BusinessException.class})
	public ResponseEntity<ResponseDTO<Object>> handleException(BusinessException e) {
		return businessExceptionHandler.handleException(e);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<Object>> handleException(MethodArgumentNotValidException e) {
		return methodArgumentNotValidExceptionHandler.handleException(e);
    }
	
	@ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ResponseDTO<Object>> handleException(InvalidFormatException e) {
		return invalidFormatExceptionHandler.handleException(e);
    }
}
