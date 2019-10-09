package com.matera.cursoferias.digitalbank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matera.cursoferias.digitalbank.business.ClienteBusiness;
import com.matera.cursoferias.digitalbank.dto.request.ClienteRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;

@Service
public class ClienteService {

	@Autowired
	private ClienteBusiness clienteBusiness;
	
	@Transactional
	public ContaResponseDTO criarClienteEConta(ClienteRequestDTO clienteRequestDTO) {
		return clienteBusiness.criarClienteEConta(clienteRequestDTO);
	}
}
