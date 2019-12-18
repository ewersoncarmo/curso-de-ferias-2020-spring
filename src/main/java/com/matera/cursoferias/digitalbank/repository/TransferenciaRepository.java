package com.matera.cursoferias.digitalbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.matera.cursoferias.digitalbank.domain.entity.Transferencia;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {

	@Query("SELECT t " +
		   "FROM   Transferencia t " +
		   "WHERE  t.lancamentoDebito.id  = :idLancamento OR " +
		   "       t.lancamentoCredito.id = :idLancamento ")
	Transferencia buscaTransferenciaPorIdLancamento(Long idLancamento);

}
