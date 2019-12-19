package com.matera.cursoferias.digitalbank.controller.base;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.matera.cursoferias.digitalbank.dto.response.ResponseDTO;
import com.matera.cursoferias.digitalbank.exception.BusinessException;
import com.matera.cursoferias.digitalbank.exception.handler.BusinessExceptionHandler;
import com.matera.cursoferias.digitalbank.exception.handler.GenericExceptionHandler;
import com.matera.cursoferias.digitalbank.exception.handler.InvalidFormatExceptionHandler;
import com.matera.cursoferias.digitalbank.exception.handler.MethodArgumentNotValidExceptionHandler;

public abstract class ControllerBase {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerBase.class);

	@Autowired
	private BusinessExceptionHandler businessExceptionHandler;

	@Autowired
	private MethodArgumentNotValidExceptionHandler methodArgumentNotValidExceptionHandler;

	@Autowired
	private InvalidFormatExceptionHandler invalidFormatExceptionHandler;

	@Autowired
	private GenericExceptionHandler genericExceptionHandler;

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ResponseDTO<Object>> handleException(BusinessException exception) {
		return businessExceptionHandler.handleException(exception);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<Object>> handleException(MethodArgumentNotValidException exception) {
		return methodArgumentNotValidExceptionHandler.handleException(exception);
    }

	@ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ResponseDTO<Object>> handleException(InvalidFormatException exception) {
		return invalidFormatExceptionHandler.handleException(exception);
    }

	@ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<Object>> handleException(Exception exception) {
	    LOG.error(ExceptionUtils.getStackTrace(exception));

	    return genericExceptionHandler.handleException(exception);
    }

}
