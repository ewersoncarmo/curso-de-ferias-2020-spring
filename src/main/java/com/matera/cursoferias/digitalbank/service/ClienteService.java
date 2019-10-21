package com.matera.cursoferias.digitalbank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matera.cursoferias.digitalbank.business.ClienteBusiness;
import com.matera.cursoferias.digitalbank.dto.request.ClienteRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;

@Service
public class ClienteService {

	@Autowired
	private ClienteBusiness clienteBusiness;
	
	public ContaResponseDTO criarCadastro(ClienteRequestDTO clienteRequestDTO) {
		return clienteBusiness.criarCadastro(clienteRequestDTO);
	}
}
