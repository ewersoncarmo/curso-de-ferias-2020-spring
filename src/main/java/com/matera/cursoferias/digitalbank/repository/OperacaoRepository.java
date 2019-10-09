package com.matera.cursoferias.digitalbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matera.cursoferias.digitalbank.domain.entity.Operacao;

@Repository
public interface OperacaoRepository extends JpaRepository<Operacao, Long> {

}
