package com.matera.cursoferias.digitalbank.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.matera.cursoferias.digitalbank.domain.entity.Cliente;
import com.matera.cursoferias.digitalbank.dto.request.ClienteRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.repository.ClienteRepository;
import com.matera.cursoferias.digitalbank.util.exceptions.BusinessException;

@Component
public class ClienteBusiness {

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private ContaBusiness contaBusiness;
	
	@Transactional
	public ContaResponseDTO criarCadastro(ClienteRequestDTO clienteRequestDTO) {
		validar(clienteRequestDTO);
		
		Cliente cliente = new Cliente();
		cliente.setNome(clienteRequestDTO.getNome());
		cliente.setCpf(clienteRequestDTO.getCpf());
		cliente.setTelefone(clienteRequestDTO.getTelefone());
		cliente.setRendaMensal(clienteRequestDTO.getRendaMensal());
		cliente.setLogradouro(clienteRequestDTO.getLogradouro());
		cliente.setNumero(clienteRequestDTO.getNumero());
		cliente.setComplemento(clienteRequestDTO.getComplemento());
		cliente.setBairro(clienteRequestDTO.getBairro());
		cliente.setCidade(clienteRequestDTO.getCidade());
		cliente.setEstado(clienteRequestDTO.getEstado());
		cliente.setCep(clienteRequestDTO.getCep());
		
		cliente = clienteRepository.save(cliente);

		return contaBusiness.criarConta(cliente);
	}

	private void validar(ClienteRequestDTO clienteRequestDTO) {
		if (clienteRepository.findByCpf(clienteRequestDTO.getCpf()) != null) {
			throw new BusinessException("Já existe um Cliente cadastrado com o CPF informado.");
		}
	}

}
