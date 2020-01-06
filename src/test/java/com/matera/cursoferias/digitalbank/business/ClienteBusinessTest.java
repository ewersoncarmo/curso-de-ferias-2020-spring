package com.matera.cursoferias.digitalbank.business;

import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildClienteEntidade;
import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildClienteRequestDTO;
import static com.matera.cursoferias.digitalbank.utils.DigitalBankTestUtils.buildContaResponseDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.matera.cursoferias.digitalbank.domain.entity.Cliente;
import com.matera.cursoferias.digitalbank.dto.request.ClienteRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ClienteResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.exception.BusinessException;
import com.matera.cursoferias.digitalbank.repository.ClienteRepository;

@ExtendWith(MockitoExtension.class)
public class ClienteBusinessTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ContaBusiness contaBusiness;

    @InjectMocks
    private ClienteBusiness clienteBusiness;

    @Test
    public void cadastraClienteComSucesso() {
        ClienteRequestDTO clienteRequest = buildClienteRequestDTO();
        ContaResponseDTO contaResponseMock = buildContaResponseDTO();

        when(clienteRepository.findByCpf(eq(clienteRequest.getCpf()))).thenReturn(Optional.empty());
        when(contaBusiness.cadastra(any(Cliente.class))).thenReturn(contaResponseMock);

        ContaResponseDTO contaResponse = clienteBusiness.cadastra(clienteRequest);

        verify(clienteRepository).findByCpf(eq(clienteRequest.getCpf()));
        verify(clienteRepository).save(any(Cliente.class));
        verify(contaBusiness).cadastra(any(Cliente.class));
        verifyNoMoreInteractions(clienteRepository);
        verifyNoMoreInteractions(contaBusiness);

        assertNotNull(contaResponse);
    }

    @Test
    public void cadastraClienteJaExistente() {
        ClienteRequestDTO clienteRequest = buildClienteRequestDTO();
        Cliente clienteMock = buildClienteEntidade();

        when(clienteRepository.findByCpf(eq(clienteRequest.getCpf()))).thenReturn(Optional.of(clienteMock));

        BusinessException businessException = assertThrows(BusinessException.class, () -> clienteBusiness.cadastra(clienteRequest));

        verify(clienteRepository).findByCpf(eq(clienteRequest.getCpf()));
        verifyNoMoreInteractions(clienteRepository);
        verifyNoInteractions(contaBusiness);

        assertEquals("DB-2", businessException.getCodigoErro());
    }

    @Test
    public void consultaClientePorIdComSucesso() {
        Cliente clienteMock = buildClienteEntidade();

        when(clienteRepository.findById(eq(clienteMock.getId()))).thenReturn(Optional.of(clienteMock));

        ClienteResponseDTO clienteResponse = clienteBusiness.consulta(clienteMock.getId());

        verify(clienteRepository).findById(eq(clienteMock.getId()));
        verifyNoMoreInteractions(clienteRepository);
        verifyNoInteractions(contaBusiness);

        assertNotNull(clienteResponse);
    }

    @Test
    public void consultaClientePorIdNaoExistente() {
        Long idCliente = 1L;

        when(clienteRepository.findById(eq(idCliente))).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> clienteBusiness.consulta(idCliente));

        verify(clienteRepository).findById(eq(idCliente));
        verifyNoMoreInteractions(clienteRepository);
        verifyNoInteractions(contaBusiness);

        assertEquals("DB-1", businessException.getCodigoErro());
    }

    @Test
    public void consultaTodosOsClientes() {
        List<Cliente> clientesMock = Arrays.asList(buildClienteEntidade(), buildClienteEntidade());

        when(clienteRepository.findAll()).thenReturn(clientesMock);

        List<ClienteResponseDTO> clientesResponse = clienteBusiness.consultaTodos();

        verify(clienteRepository).findAll();
        verifyNoMoreInteractions(clienteRepository);
        verifyNoInteractions(contaBusiness);

        assertEquals(clientesMock.size(), clientesResponse.size());
    }

    @Test
    public void atualizaClienteComSucesso() {
        Cliente clienteMock = buildClienteEntidade();
        ClienteRequestDTO clienteRequest = buildClienteRequestDTO();

        when(clienteRepository.findById(eq(clienteMock.getId()))).thenReturn(Optional.of(clienteMock));

        clienteBusiness.atualiza(clienteMock.getId(), clienteRequest);

        verify(clienteRepository).findById(eq(clienteMock.getId()));
        verify(clienteRepository).save(any(Cliente.class));
        verifyNoMoreInteractions(clienteRepository);
        verifyNoInteractions(contaBusiness);
    }

    @Test
    public void atualizaClienteNaoExistente() {
        Long idCliente = 1L;
        ClienteRequestDTO clienteRequest = buildClienteRequestDTO();

        when(clienteRepository.findById(eq(idCliente))).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class, () -> clienteBusiness.atualiza(idCliente, clienteRequest));

        verify(clienteRepository).findById(eq(idCliente));
        verifyNoMoreInteractions(clienteRepository);
        verifyNoInteractions(contaBusiness);

        assertEquals("DB-1", businessException.getCodigoErro());
    }

    @Test
    public void consultaContaPorIdCliente() {
        Long idCliente = 1L;
        ContaResponseDTO contaResponseMock = buildContaResponseDTO();

        when(contaBusiness.consultaContaPorIdCliente(eq(idCliente))).thenReturn(contaResponseMock);

        ContaResponseDTO contaResponse = clienteBusiness.consultaContaPorIdCliente(idCliente);

        verify(contaBusiness).consultaContaPorIdCliente(eq(idCliente));
        verifyNoMoreInteractions(contaBusiness);
        verifyNoInteractions(clienteRepository);

        assertNotNull(contaResponse);
    }

}
