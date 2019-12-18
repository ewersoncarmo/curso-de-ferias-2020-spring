package com.matera.cursoferias.digitalbank.domain.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.matera.cursoferias.digitalbank.domain.entity.base.EntidadeBase;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
public class Estorno extends EntidadeBase {

	@OneToOne
	@JoinColumn(name = "id_lancamento_original")
	private Lancamento lancamentoOriginal;

	@OneToOne
	@JoinColumn(name = "id_lancamento_estorno")
	private Lancamento lancamentoEstorno;

}
