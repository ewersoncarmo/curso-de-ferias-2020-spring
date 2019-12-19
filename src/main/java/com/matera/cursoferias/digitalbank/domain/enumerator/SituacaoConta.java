package com.matera.cursoferias.digitalbank.domain.enumerator;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SituacaoConta {

    ABERTA("A"),
    BLOQUEADA("B");

    private String codigo;

    private SituacaoConta(String codigo) {
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
