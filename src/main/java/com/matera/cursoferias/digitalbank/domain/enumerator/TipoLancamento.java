package com.matera.cursoferias.digitalbank.domain.enumerator;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoLancamento {

    DEPOSITO("D"),
    SAQUE("S"),
    TRANSFERENCIA("T"),
    PAGAMENTO("P"),
    ESTORNO("E");

    private String codigo;

    private TipoLancamento(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(codigo);
    }

}
