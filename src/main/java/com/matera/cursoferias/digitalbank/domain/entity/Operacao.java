package com.matera.cursoferias.digitalbank.domain.entity;

import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.matera.cursoferias.digitalbank.domain.commons.Transacao;
import com.matera.cursoferias.digitalbank.domain.entity.base.EntidadeBase;

@Entity
public class Operacao extends EntidadeBase {

	@Embedded
	private Transacao transacao;

	public Transacao getTransacao() {
		return transacao;
	}

	public void setTransacao(Transacao transacao) {
		this.transacao = transacao;
	}
	
}
