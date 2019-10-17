package com.matera.cursoferias.digitalbank.domain.enumerator;

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

}
