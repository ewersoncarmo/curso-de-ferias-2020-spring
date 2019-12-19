package com.matera.cursoferias.digitalbank.exception.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.matera.cursoferias.digitalbank.dto.response.ErroResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ResponseDTO;

@Component
public class GenericExceptionHandler implements ExceptionHandler<Exception> {

	@Autowired
	private MessageSource messageSource;

	@Override
	public ResponseEntity<ResponseDTO<Object>> handleException(Exception exception) {
		String mensagemErro = messageSource.getMessage("DB-99", null, LocaleContextHolder.getLocale());
		ErroResponseDTO erro = new ErroResponseDTO("DB-99: " + mensagemErro);

		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ResponseDTO.comErro(erro));
	}

}
