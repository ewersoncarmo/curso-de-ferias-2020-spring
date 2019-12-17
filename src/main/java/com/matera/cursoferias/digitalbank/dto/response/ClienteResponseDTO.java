package com.matera.cursoferias.digitalbank.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
