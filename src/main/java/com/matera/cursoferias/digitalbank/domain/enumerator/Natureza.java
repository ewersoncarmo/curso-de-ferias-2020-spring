package com.matera.cursoferias.digitalbank.domain.enumerator;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Natureza {

    CREDITO("C"),
    DEBITO("D");

    private String codigo;

    private Natureza(String codigo) {
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
