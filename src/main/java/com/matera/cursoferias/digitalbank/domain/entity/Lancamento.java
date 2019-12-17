package com.matera.cursoferias.digitalbank.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.matera.cursoferias.digitalbank.domain.entity.base.EntidadeBase;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Lancamento extends EntidadeBase {

	@Column
	private String codigoAutenticacao;

	@Column
	private LocalDateTime dataHora;

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

}
