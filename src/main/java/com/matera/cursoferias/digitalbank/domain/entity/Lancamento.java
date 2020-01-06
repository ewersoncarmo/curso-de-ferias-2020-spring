package com.matera.cursoferias.digitalbank.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "db_lancamento")
public class Lancamento extends EntidadeBase {

    @Builder
	public Lancamento(Long id, String codigoAutenticacao, LocalDateTime dataHora, BigDecimal valor, String natureza,
            String tipoLancamento, String descricao, Conta conta) {
        super(id);
        this.codigoAutenticacao = codigoAutenticacao;
        this.dataHora = dataHora;
        this.valor = valor;
        this.natureza = natureza;
        this.tipoLancamento = tipoLancamento;
        this.descricao = descricao;
        this.conta = conta;
    }

    @Column(length = 50, nullable = false)
	private String codigoAutenticacao;

	@Column(nullable = false)
	private LocalDateTime dataHora;

	@Column(precision = 20, scale = 2, nullable = false)
	private BigDecimal valor;

	@Column(length = 1, nullable = false)
	private String natureza;

	@Column(length = 1, nullable = false)
	private String tipoLancamento;

	@Column(length = 50, nullable = true)
	private String descricao;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_conta")
	private Conta conta;

}
