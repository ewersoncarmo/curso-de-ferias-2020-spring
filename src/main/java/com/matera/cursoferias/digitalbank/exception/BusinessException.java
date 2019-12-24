package com.matera.cursoferias.digitalbank.exception;

import java.util.Arrays;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String codigoErro;
	private final Object[] parametros;

	public static void main(String[] args) {
		Object[] parametros = new Object[] {"1", "2"};
		System.out.println(Arrays.toString(parametros));
	}
	
	public BusinessException(String codigoErro, Object... parametros) {
		super(codigoErro + " - " + Arrays.toString(parametros));

		this.codigoErro = codigoErro;
		this.parametros = parametros;
	}

}
