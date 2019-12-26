package com.matera.cursoferias.digitalbank.utils;

import java.math.BigDecimal;

import com.matera.cursoferias.digitalbank.domain.enumerator.Natureza;

public class DigitalBankUtils {

    private DigitalBankUtils() { }

	public static BigDecimal calculaSaldo(Natureza natureza, BigDecimal valor, BigDecimal saldoAtual) {
		BigDecimal saldoFinal;

		if (Natureza.DEBITO.equals(natureza)) {
			saldoFinal = saldoAtual.subtract(valor);
		} else {
			saldoFinal = saldoAtual.add(valor);
		}

		return saldoFinal;
	}

}
