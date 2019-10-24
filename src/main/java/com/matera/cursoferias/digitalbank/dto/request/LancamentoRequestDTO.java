package com.matera.cursoferias.digitalbank.dto.request;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

public class LancamentoRequestDTO {

	@NotNull
	@Digits(integer = 18, fraction = 2)
	private BigDecimal valor;
	
	private String descricao;

	public LancamentoRequestDTO() {}
	
	public LancamentoRequestDTO(BigDecimal valor, String descricao) {
		this.valor = valor;
		this.descricao = descricao;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
}
