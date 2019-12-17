package com.matera.cursoferias.digitalbank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matera.cursoferias.digitalbank.domain.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

	public List<Lancamento> findByConta_Id(Long id);

}
