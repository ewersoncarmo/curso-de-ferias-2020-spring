package com.matera.cursoferias.digitalbank.exception;

public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 3593690214087645447L;

	public BusinessException(String mensagem) {
		super(mensagem);
	}
}
