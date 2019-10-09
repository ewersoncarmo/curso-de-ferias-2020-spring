package com.matera.cursoferias.digitalbank.controller.base;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.matera.cursoferias.digitalbank.dto.response.ErroResponseDTO;
import com.matera.cursoferias.digitalbank.util.BusinessException;

public class ControllerBase {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({BusinessException.class})
	public ErroResponseDTO handleBusinessException(BusinessException e) {
		return ErroResponseDTO.withError(e.getMessage());
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({MethodArgumentNotValidException.class}) 
	public ErroResponseDTO handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		return ErroResponseDTO.withError(e.getBindingResult().getFieldErrors());
	}
}
