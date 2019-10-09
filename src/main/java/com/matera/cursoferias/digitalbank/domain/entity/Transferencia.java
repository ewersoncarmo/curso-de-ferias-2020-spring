package com.matera.cursoferias.digitalbank.domain.entity;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.matera.cursoferias.digitalbank.domain.commons.Transacao;
import com.matera.cursoferias.digitalbank.domain.entity.base.EntidadeBase;

@Entity
public class Transferencia extends EntidadeBase {

	@Embedded
	private Transacao transacao;

	@ManyToOne
	@JoinColumn(name = "id_conta_destino")
	private Conta contaDestino;

	public Transacao getTransacao() {
		return transacao;
	}

	public void setTransacao(Transacao transacao) {
		this.transacao = transacao;
	}

	public Conta getContaDestino() {
		return contaDestino;
	}

	public void setContaDestino(Conta contaDestino) {
		this.contaDestino = contaDestino;
	}
	
}
