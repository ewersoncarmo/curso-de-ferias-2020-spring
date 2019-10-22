package com.matera.cursoferias.digitalbank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matera.cursoferias.digitalbank.business.ClienteBusiness;
import com.matera.cursoferias.digitalbank.dto.request.ClienteRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ClienteResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;

@Service
public class ClienteService {

	@Autowired
	private ClienteBusiness clienteBusiness;
	
	public ContaResponseDTO cadastrar(ClienteRequestDTO clienteRequestDTO) {
		return clienteBusiness.cadastrar(clienteRequestDTO);
	}

	public ClienteResponseDTO consultar(Long id) {
		return clienteBusiness.consultar(id);
	}

	public void atualizar(Long id, ClienteRequestDTO clienteRequestDTO) {
		clienteBusiness.atualizar(id, clienteRequestDTO);
	}
}
