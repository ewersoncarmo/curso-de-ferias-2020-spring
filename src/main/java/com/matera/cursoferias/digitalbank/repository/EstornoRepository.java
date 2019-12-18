package com.matera.cursoferias.digitalbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matera.cursoferias.digitalbank.domain.entity.Estorno;

public interface EstornoRepository extends JpaRepository<Estorno, Long> {

}
