package com.matera.cursoferias.digitalbank.domain.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.matera.cursoferias.digitalbank.domain.entity.base.EntidadeBase;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Cliente extends EntidadeBase {

	@Column
	private String nome;

	@Column
	private String cpf;

	@Column
	private Long telefone;

	@Column
	private BigDecimal rendaMensal;

	@Column
	private String logradouro;

	@Column
	private Integer numero;

	@Column
	private String complemento;

	@Column
	private String bairro;

	@Column
	private String cidade;

	@Column
	private String estado;

	@Column
	private String cep;

}
