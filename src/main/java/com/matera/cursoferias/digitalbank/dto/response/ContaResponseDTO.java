package com.matera.cursoferias.digitalbank.dto.response;

import java.math.BigDecimal;

public class ContaResponseDTO {

	private Long id;
	private Integer numeroAgencia;
	private Long numeroConta;
	private BigDecimal saldo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNumeroAgencia() {
		return numeroAgencia;
	}

	public void setNumeroAgencia(Integer numeroAgencia) {
		this.numeroAgencia = numeroAgencia;
	}

	public Long getNumeroConta() {
		return numeroConta;
	}

	public void setNumeroConta(Long numeroConta) {
		this.numeroConta = numeroConta;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

}
