package com.matera.cursoferias.digitalbank.exception.handler;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.matera.cursoferias.digitalbank.dto.response.ErroResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ResponseDTO;

@Component
public class GenericExceptionHandler implements ExceptionHandler<Exception> {

	private final MessageSource messageSource;

	public GenericExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
	public ResponseEntity<ResponseDTO<Object>> handleException(Exception exception) {
		String mensagemErro = messageSource.getMessage("DB-99", null, LocaleContextHolder.getLocale());
		ErroResponseDTO erro = new ErroResponseDTO("DB-99: " + mensagemErro);

		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ResponseDTO.comErro(erro));
	}

}
