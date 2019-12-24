package com.matera.cursoferias.digitalbank.domain.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.matera.cursoferias.digitalbank.domain.entity.base.EntidadeBase;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "db_transferencia")
public class Transferencia extends EntidadeBase {

    @OneToOne
	@JoinColumn(name = "id_lancamento_debito", nullable = false)
	private Lancamento lancamentoDebito;

    @OneToOne
	@JoinColumn(name = "id_lancamento_credito", nullable = false)
    private Lancamento lancamentoCredito;

}
