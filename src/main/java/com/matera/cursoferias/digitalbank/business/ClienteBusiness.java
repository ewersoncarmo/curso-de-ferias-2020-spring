package com.matera.cursoferias.digitalbank.business;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.matera.cursoferias.digitalbank.domain.entity.Cliente;
import com.matera.cursoferias.digitalbank.dto.request.ClienteRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ClienteResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.exception.BusinessException;
import com.matera.cursoferias.digitalbank.repository.ClienteRepository;

@Component
public class ClienteBusiness {

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private ContaBusiness contaBusiness;

	@Transactional
	public ContaResponseDTO cadastra(ClienteRequestDTO clienteRequestDTO) {
		valida(clienteRequestDTO);

		Cliente cliente = requestDTOParaEntidade(clienteRequestDTO, new Cliente());

		cliente = clienteRepository.save(cliente);

		return contaBusiness.cadastra(cliente);
	}

	public ClienteResponseDTO consulta(Long id) {
		Cliente cliente = findById(id);

		return entidadeParaResponseDTO(cliente);
	}

	public List<ClienteResponseDTO> consultaTodos() {
        List<Cliente> clientes = clienteRepository.findAll();
        List<ClienteResponseDTO> clientesResponseDTO = new ArrayList<>();

        clientes.forEach(c -> clientesResponseDTO.add(entidadeParaResponseDTO(c)));

        return clientesResponseDTO;
    }

	public void atualiza(Long id, ClienteRequestDTO clienteRequestDTO) {
		Cliente cliente = requestDTOParaEntidade(clienteRequestDTO, findById(id));

		clienteRepository.save(cliente);
	}

	public ContaResponseDTO consultaContaPorIdCliente(Long idCliente) {
	    return contaBusiness.consultaContaPorIdCliente(idCliente);
	}

	private Cliente findById(Long id) {
		return clienteRepository.findById(id).orElseThrow(() -> new BusinessException("DB-1", id));
	}

	private void valida(ClienteRequestDTO clienteRequestDTO) {
		if (clienteRepository.findByCpf(clienteRequestDTO.getCpf()).isPresent()) {
			throw new BusinessException("DB-2", clienteRequestDTO.getCpf());
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

	private ClienteResponseDTO entidadeParaResponseDTO(Cliente cliente) {
        return ClienteResponseDTO.builder().id(cliente.getId())
                                           .nome(cliente.getNome())
                                           .cpf(cliente.getCpf())
                                           .telefone(cliente.getTelefone())
                                           .rendaMensal(cliente.getRendaMensal())
                                           .logradouro(cliente.getLogradouro())
                                           .numero(cliente.getNumero())
                                           .complemento(cliente.getComplemento())
                                           .bairro(cliente.getBairro())
                                           .cidade(cliente.getCidade())
                                           .estado(cliente.getEstado())
                                           .cep(cliente.getCep())
                                           .build();
    }

}