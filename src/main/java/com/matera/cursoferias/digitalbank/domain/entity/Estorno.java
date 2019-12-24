package com.matera.cursoferias.digitalbank.domain.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
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
@Table(name = "db_estorno")
public class Estorno extends EntidadeBase {

	@OneToOne
	@JoinColumn(name = "id_lancamento_original", nullable = false)
	private Lancamento lancamentoOriginal;

	@OneToOne
	@JoinColumn(name = "id_lancamento_estorno", nullable = false)
	private Lancamento lancamentoEstorno;

}
