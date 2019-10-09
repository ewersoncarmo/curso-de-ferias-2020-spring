package com.matera.cursoferias.digitalbank.domain.commons;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.matera.cursoferias.digitalbank.domain.entity.Conta;
import com.matera.cursoferias.digitalbank.domain.enumerator.Natureza;

@Embeddable
public class Transacao {

	private LocalDate data;
	private BigDecimal valor;
	private Natureza natureza;
	private String descricao;
	
	@ManyToOne
	@JoinColumn(name = "id_conta_origem")
	private Conta contaOrigem;

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public Natureza getNatureza() {
		return natureza;
	}

	public void setNatureza(Natureza natureza) {
		this.natureza = natureza;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Conta getContaOrigem() {
		return contaOrigem;
	}

	public void setContaOrigem(Conta contaOrigem) {
		this.contaOrigem = contaOrigem;
	}
	
}
