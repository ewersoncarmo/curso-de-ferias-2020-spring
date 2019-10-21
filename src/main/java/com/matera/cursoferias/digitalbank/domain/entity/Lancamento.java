package com.matera.cursoferias.digitalbank.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.matera.cursoferias.digitalbank.domain.entity.base.EntidadeBase;

@Entity
public class Lancamento extends EntidadeBase {

	@Column
	private LocalDate data;
	
	@Column
	private BigDecimal valor;
	
	@Column
	private String natureza;
	
	@Column
	private String tipoLancamento;
	
	@Column
	private String descricao;

	@ManyToOne
	@JoinColumn(name = "id_conta")
	private Conta conta;

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

	public String getNatureza() {
		return natureza;
	}

	public void setNatureza(String natureza) {
		this.natureza = natureza;
	}

	public String getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(String tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

}
