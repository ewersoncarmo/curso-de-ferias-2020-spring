package com.matera.cursoferias.digitalbank.dto.request;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferenicaRequestDTO {

	@NotNull
	private Integer numeroAgencia;

	@NotNull
	private Long numeroConta;

	@NotNull
	@Digits(integer = 18, fraction = 2)
	private BigDecimal valor;

	private String descricao;

}
