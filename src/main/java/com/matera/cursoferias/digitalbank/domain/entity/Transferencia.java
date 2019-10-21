package com.matera.cursoferias.digitalbank.domain.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.matera.cursoferias.digitalbank.domain.entity.base.EntidadeBase;

@Entity
public class Transferencia extends EntidadeBase {

    @OneToOne
	@JoinColumn(name = "id_lancamento_debito")
	private Lancamento lancamentoDebito;

    @OneToOne
	@JoinColumn(name = "id_lancamento_credito")
    private Lancamento lancamentoCredito;

    public Lancamento getLancamentoDebito() {
        return lancamentoDebito;
    }

    public void setLancamentoDebito(Lancamento lancamentoDebito) {
        this.lancamentoDebito = lancamentoDebito;
    }

    public Lancamento getLancamentoCredito() {
        return lancamentoCredito;
    }

    public void setLancamentoCredito(Lancamento lancamentoCredito) {
        this.lancamentoCredito = lancamentoCredito;
    }

}
