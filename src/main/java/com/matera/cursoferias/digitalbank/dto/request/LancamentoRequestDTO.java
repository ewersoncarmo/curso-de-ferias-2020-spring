package com.matera.cursoferias.digitalbank.dto.request;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LancamentoRequestDTO {

	@NotNull
	@Digits(integer = 18, fraction = 2)
	private BigDecimal valor;

	private String descricao;

}
