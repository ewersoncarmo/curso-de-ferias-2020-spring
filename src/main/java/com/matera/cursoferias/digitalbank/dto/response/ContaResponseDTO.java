package com.matera.cursoferias.digitalbank.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ContaResponseDTO {

    private Long idCliente;
	private Long idConta;
	private Integer numeroAgencia;
	private Long numeroConta;
	private BigDecimal saldo;

}
