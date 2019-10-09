package com.matera.cursoferias.digitalbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matera.cursoferias.digitalbank.domain.entity.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

	public Cliente findByCpf(String cpf);

}
