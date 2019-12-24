package com.matera.cursoferias.digitalbank.exception.handler;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.matera.cursoferias.digitalbank.dto.response.ErroResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ResponseDTO;
import com.matera.cursoferias.digitalbank.exception.BusinessException;

@Component
public class BusinessExceptionHandler implements ExceptionHandler<BusinessException> {

	private final MessageSource messageSource;

	public BusinessExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
	public ResponseEntity<ResponseDTO<Object>> handleException(BusinessException exception) {
		String mensagemErro = messageSource.getMessage(exception.getCodigoErro(), exception.getParametros(), LocaleContextHolder.getLocale());
		ErroResponseDTO erro = new ErroResponseDTO(exception.getCodigoErro() + ": " + mensagemErro);

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ResponseDTO.comErro(erro));
	}

}
