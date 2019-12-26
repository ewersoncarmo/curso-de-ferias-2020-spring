package com.matera.cursoferias.digitalbank.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.matera.cursoferias.digitalbank.business.ContaBusiness;

@Component
public class JobSorteio {

    private static final Logger LOG = LoggerFactory.getLogger(JobSorteio.class);

    private final ContaBusiness contaBusiness;

    public JobSorteio(ContaBusiness contaBusiness) {
        this.contaBusiness = contaBusiness;
    }

    @Scheduled(cron = "${job.sorteio.cron}")
    public void realizaSorteio() {
        LOG.info("Iniciando job de sorteio");

        Long contaSorteada = contaBusiness.realizaSorteio();

        LOG.info("Finalizando job de sorteio. Id da conta sorteada: {}", contaSorteada);
    }

}
