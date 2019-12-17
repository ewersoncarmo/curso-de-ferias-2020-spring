package com.matera.cursoferias.digitalbank.dto.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteResponseDTO {

	private Long id;
	private String nome;
	private String cpf;
	private Long telefone;
	private BigDecimal rendaMensal;
	private String logradouro;
	private Integer numero;
	private String complemento;
	private String bairro;
	private String cidade;
	private String cep;

}
