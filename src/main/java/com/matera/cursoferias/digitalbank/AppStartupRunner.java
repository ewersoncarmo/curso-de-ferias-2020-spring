package com.matera.cursoferias.digitalbank;

import java.math.BigDecimal;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.matera.cursoferias.digitalbank.domain.enumerator.TipoLancamento;
import com.matera.cursoferias.digitalbank.dto.request.ClienteRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.TransferenciaRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ComprovanteResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.service.ClienteService;
import com.matera.cursoferias.digitalbank.service.ContaService;

@Component
public class AppStartupRunner implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(AppStartupRunner.class);

    private final ClienteService clienteService;
    private final ContaService contaService;
    private final Environment environment;

    public AppStartupRunner(ClienteService clienteService, ContaService contaService, Environment environment) {
        this.clienteService = clienteService;
        this.contaService = contaService;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            LOG.info("Iniciando AppStartupRunner");


            ContaResponseDTO cliente1 = clienteService.cadastra(ClienteRequestDTO.builder().nome("Cliente 1")
                                                                                           .cpf("72979929921")
                                                                                           .telefone(44999001234L)
                                                                                           .rendaMensal(BigDecimal.valueOf(5000))
                                                                                           .logradouro("Rua 1")
                                                                                           .numero(100)
                                                                                           .complemento("Casa 1")
                                                                                           .bairro("Bairro 1")
                                                                                           .cidade("Maringá")
                                                                                           .estado("PR")
                                                                                           .cep("87087087")
                                                                                           .build());

            ContaResponseDTO cliente2 = clienteService.cadastra(ClienteRequestDTO.builder().nome("Cliente 2")
                                                                                           .cpf("50667427945")
                                                                                           .telefone(44999001235L)
                                                                                           .rendaMensal(BigDecimal.valueOf(6000))
                                                                                           .logradouro("Rua 2")
                                                                                           .numero(200)
                                                                                           .complemento("Casa 2")
                                                                                           .bairro("Bairro 2")
                                                                                           .cidade("Maringá")
                                                                                           .estado("PR")
                                                                                           .cep("87087088")
                                                                                           .build());

            contaService.efetuaLancamento(cliente1.getIdConta(),
                                          LancamentoRequestDTO.builder()
                                                              .descricao("Depósito Caixa Eletrônico")
                                                              .valor(BigDecimal.valueOf(1000))
                                                              .build(),
                                          TipoLancamento.DEPOSITO);

            contaService.efetuaLancamento(cliente1.getIdConta(),
                                          LancamentoRequestDTO.builder()
                                                              .descricao("Saque Caixa Eletrônico")
                                                              .valor(BigDecimal.valueOf(100))
                                                              .build(),
                                          TipoLancamento.SAQUE);

            ComprovanteResponseDTO lancamento3 = contaService.efetuaLancamento(cliente1.getIdConta(),
                                                                               LancamentoRequestDTO.builder()
                                                                                                   .descricao("Pagamento de Boleto")
                                                                                                   .valor(BigDecimal.valueOf(50))
                                                                                                   .build(),
                                                                               TipoLancamento.PAGAMENTO);

            contaService.efetuaTransferencia(cliente1.getIdConta(),
                                             TransferenciaRequestDTO.builder()
                                                                    .descricao("Churrasco")
                                                                    .numeroAgencia(cliente2.getNumeroAgencia())
                                                                    .numeroConta(cliente2.getNumeroConta())
                                                                    .valor(BigDecimal.valueOf(30)).build());

            contaService.estornaLancamento(cliente1.getIdConta(), lancamento3.getIdLancamento());

            LOG.info("Finalizando AppStartupRunner");
        } else {
            LOG.info("AppStartupRunner não será executado para testes automáticos");
        }
    }

}
