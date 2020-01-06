package com.matera.cursoferias.digitalbank.domain.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.matera.cursoferias.digitalbank.domain.entity.base.EntidadeBase;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "db_cliente")
public class Cliente extends EntidadeBase {

    @Builder
	public Cliente(Long id, String nome, String cpf, Long telefone, BigDecimal rendaMensal, String logradouro,
            Integer numero, String complemento, String bairro, String cidade, String estado, String cep) {
        super(id);
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.rendaMensal = rendaMensal;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
    }

    @Column(length = 100, nullable = false)
	private String nome;

	@Column(length = 11, nullable = false)
	private String cpf;

	@Column(precision = 12, nullable = false)
	private Long telefone;

	@Column(precision = 20, scale = 2, nullable = false)
	private BigDecimal rendaMensal;

	@Column(length = 100, nullable = false)
	private String logradouro;

	@Column(precision = 5, nullable = false)
	private Integer numero;

	@Column(length = 100, nullable = true)
	private String complemento;

	@Column(length = 100, nullable = false)
	private String bairro;

	@Column(length = 100, nullable = false)
	private String cidade;

	@Column(length = 2, nullable = false)
	private String estado;

	@Column(length = 8, nullable = false)
	private String cep;

}
