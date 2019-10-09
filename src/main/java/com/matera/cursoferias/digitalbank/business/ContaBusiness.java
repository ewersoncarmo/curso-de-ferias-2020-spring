package com.matera.cursoferias.digitalbank.business;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.matera.cursoferias.digitalbank.domain.entity.Cliente;
import com.matera.cursoferias.digitalbank.domain.entity.Conta;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.repository.ContaRepository;
import com.matera.cursoferias.digitalbank.util.BusinessException;

@Component
public class ContaBusiness {

	@Autowired
	private ContaRepository contaRepository;
	
	public ContaResponseDTO criarConta(Cliente cliente) {
		validar(cliente);
		
		Conta conta = new Conta();
		conta.setNumeroAgencia(1);
		conta.setNumeroConta(cliente.getTelefone());
		conta.setSaldo(new BigDecimal(0));
		conta.setCliente(cliente);
		
		conta = contaRepository.save(conta);

		ContaResponseDTO contaResponseDTO = new ContaResponseDTO();
		contaResponseDTO.setId(conta.getId());
		contaResponseDTO.setNumeroAgencia(conta.getNumeroAgencia());
		contaResponseDTO.setNumeroConta(conta.getNumeroConta());
		contaResponseDTO.setSaldo(conta.getSaldo());
		
		return contaResponseDTO;
	}

	private void validar(Cliente cliente) {
		if (contaRepository.findByNumeroConta(cliente.getTelefone()) != null) {
			throw new BusinessException("Já existe uma Conta cadastrada com o número informado.");
		}
	}

}
