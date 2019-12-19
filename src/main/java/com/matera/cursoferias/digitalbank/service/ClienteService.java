package com.matera.cursoferias.digitalbank.service;

import java.util.List;

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

	public ContaResponseDTO cadastra(ClienteRequestDTO clienteRequestDTO) {
		return clienteBusiness.cadastra(clienteRequestDTO);
	}

	public ClienteResponseDTO consultaPorId(Long id) {
		return clienteBusiness.consulta(id);
	}

	public List<ClienteResponseDTO> consultaTodos() {
        return clienteBusiness.consultaTodos();
    }

	public void atualiza(Long id, ClienteRequestDTO clienteRequestDTO) {
		clienteBusiness.atualiza(id, clienteRequestDTO);
	}

	public ContaResponseDTO consultaContaPorIdCliente(Long idCliente) {
        return clienteBusiness.consultaContaPorIdCliente(idCliente);
    }
}
