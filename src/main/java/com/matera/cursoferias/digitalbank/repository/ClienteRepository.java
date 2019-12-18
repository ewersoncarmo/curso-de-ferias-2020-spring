package com.matera.cursoferias.digitalbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matera.cursoferias.digitalbank.domain.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

	Cliente findByCpf(String cpf);

}
