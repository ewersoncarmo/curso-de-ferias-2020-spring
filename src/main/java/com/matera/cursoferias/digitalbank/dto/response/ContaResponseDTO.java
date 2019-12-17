package com.matera.cursoferias.digitalbank.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContaResponseDTO {

	private Long id;
	private Integer numeroAgencia;
	private Long numeroConta;
	private BigDecimal saldo;

}
