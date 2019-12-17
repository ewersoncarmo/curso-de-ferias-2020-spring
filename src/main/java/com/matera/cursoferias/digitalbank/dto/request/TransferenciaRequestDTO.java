package com.matera.cursoferias.digitalbank.dto.request;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferenciaRequestDTO {

	@NotNull
	private Integer numeroAgencia;

	@NotNull
	private Long numeroConta;

	@NotNull
	@Digits(integer = 18, fraction = 2)
	private BigDecimal valor;

	private String descricao;

}
