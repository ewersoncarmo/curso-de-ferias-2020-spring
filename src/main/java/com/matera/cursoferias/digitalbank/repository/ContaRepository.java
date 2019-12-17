package com.matera.cursoferias.digitalbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matera.cursoferias.digitalbank.domain.entity.Conta;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    Conta findByNumeroAgenciaAndNumeroConta(Integer numeroAgencia, Long numeroConta);

	Conta findByNumeroConta(Long numeroConta);

}
