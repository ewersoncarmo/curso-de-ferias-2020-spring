package com.matera.cursoferias.digitalbank.domain.entity;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
@Table(name = "db_conta")
public class Conta extends EntidadeBase {

	@Column(precision = 4, nullable = false)
	private Integer numeroAgencia;

	@Column(precision = 12, nullable = false)
	private Long numeroConta;

	@Column(precision = 20, scale = 2, nullable = false)
	private BigDecimal saldo;

	@Column(length = 1, nullable = false)
    private String situacao;

	@OneToOne
	@JoinColumn(name = "id_cliente", nullable = false)
	private Cliente cliente;

	@OneToMany(mappedBy = "conta")
	private List<Lancamento> lancamentos;

}
