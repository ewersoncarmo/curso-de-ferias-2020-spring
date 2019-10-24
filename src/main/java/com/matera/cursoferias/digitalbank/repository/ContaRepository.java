package com.matera.cursoferias.digitalbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matera.cursoferias.digitalbank.domain.entity.Conta;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

	public Conta findByNumeroAgenciaAndNumeroConta(Integer numeroAgencia, Long numeroConta);

}
