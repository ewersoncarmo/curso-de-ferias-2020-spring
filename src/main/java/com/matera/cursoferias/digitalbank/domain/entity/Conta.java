package com.matera.cursoferias.digitalbank.domain.entity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.matera.cursoferias.digitalbank.domain.commons.Transacao;
import com.matera.cursoferias.digitalbank.domain.entity.base.EntidadeBase;

@Entity
public class Conta extends EntidadeBase {

	@Column(name = "numero_agencia")
	private Integer numeroAgencia;
	
	@Column(name = "numero_conta")
	private Long numeroConta;
	
	@Column
	private BigDecimal saldo;

	@OneToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	@Transient
	private List<Transacao> transacoes;
	
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

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<Transacao> getTransacoes() {
		return Collections.unmodifiableList(transacoes);
	}

	public void addTransacao(Transacao transacao) {
		this.transacoes.add(transacao);
	}

}
