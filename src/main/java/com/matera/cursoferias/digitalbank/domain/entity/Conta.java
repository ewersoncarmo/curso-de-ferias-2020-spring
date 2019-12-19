package com.matera.cursoferias.digitalbank.domain.entity;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.matera.cursoferias.digitalbank.domain.entity.base.EntidadeBase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Conta extends EntidadeBase {

	@Column
	private Integer numeroAgencia;

	@Column
	private Long numeroConta;

	@Column
	private BigDecimal saldo;

	@OneToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;

	@Column
	private String situacao;

	@OneToMany(mappedBy = "conta")
	private List<Lancamento> lancamentos;

}
