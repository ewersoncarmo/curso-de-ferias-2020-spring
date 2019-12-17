package com.matera.cursoferias.digitalbank.dto.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContaResponseDTO {

	private Long id;
	private Integer numeroAgencia;
	private Long numeroConta;
	private BigDecimal saldo;

}
