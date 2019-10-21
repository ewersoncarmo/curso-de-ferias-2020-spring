package com.matera.cursoferias.digitalbank.util.exceptions;

public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 3593690214087645447L;

	public BusinessException(String mensagem) {
		super(mensagem);
	}
}
