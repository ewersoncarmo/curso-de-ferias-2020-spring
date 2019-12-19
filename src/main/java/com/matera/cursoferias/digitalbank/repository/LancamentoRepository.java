package com.matera.cursoferias.digitalbank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matera.cursoferias.digitalbank.domain.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

	List<Lancamento> findByConta_IdOrderByIdDesc(Long idConta);

	Optional<Lancamento> findByIdAndConta_Id(Long idLancamento, Long idConta);

}
