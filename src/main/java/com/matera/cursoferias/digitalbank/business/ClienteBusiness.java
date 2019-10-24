package com.matera.cursoferias.digitalbank.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.matera.cursoferias.digitalbank.domain.entity.Cliente;
import com.matera.cursoferias.digitalbank.dto.request.ClienteRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ClienteResponseDTO;
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
	public ContaResponseDTO cadastrar(ClienteRequestDTO clienteRequestDTO) {
		validar(clienteRequestDTO);
		
		Cliente cliente = requestDTOParaEntidade(clienteRequestDTO, new Cliente());
		
		cliente = clienteRepository.save(cliente);

		return contaBusiness.cadastrar(cliente);
	}

	public ClienteResponseDTO consultar(Long id) {
		Cliente cliente = findById(id);
		
		ClienteResponseDTO clienteResponseDTO = new ClienteResponseDTO();
		clienteResponseDTO.setId(cliente.getId());
		clienteResponseDTO.setNome(cliente.getNome());
		clienteResponseDTO.setCpf(cliente.getCpf());
		clienteResponseDTO.setTelefone(cliente.getTelefone());
		clienteResponseDTO.setRendaMensal(cliente.getRendaMensal());
		clienteResponseDTO.setLogradouro(cliente.getLogradouro());
		clienteResponseDTO.setNumero(cliente.getNumero());
		clienteResponseDTO.setComplemento(cliente.getComplemento());
		clienteResponseDTO.setBairro(cliente.getBairro());
		clienteResponseDTO.setCidade(cliente.getCidade());
		clienteResponseDTO.setCep(cliente.getCep());
		
		return clienteResponseDTO;
	}

	public void atualizar(Long id, ClienteRequestDTO clienteRequestDTO) {
		Cliente cliente = requestDTOParaEntidade(clienteRequestDTO, findById(id));
		
		clienteRepository.save(cliente);
	}
	
	private Cliente findById(Long id) {
		return clienteRepository.findById(id).orElseThrow(() -> new BusinessException(String.format("Cliente %d não encontrado", id)));
	}
	
	private void validar(ClienteRequestDTO clienteRequestDTO) {
		if (clienteRepository.findByCpf(clienteRequestDTO.getCpf()) != null) {
			throw new BusinessException("Já existe um Cliente cadastrado com o CPF informado.");
		}
	}

	private Cliente requestDTOParaEntidade(ClienteRequestDTO clienteRequestDTO, Cliente cliente) {
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
		
		return cliente;
	}

}