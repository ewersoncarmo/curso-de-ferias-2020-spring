package com.matera.cursoferias.digitalbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matera.cursoferias.digitalbank.domain.entity.Transferencia;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {

}
