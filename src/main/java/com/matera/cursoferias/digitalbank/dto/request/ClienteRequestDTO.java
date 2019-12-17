package com.matera.cursoferias.digitalbank.dto.request;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.br.CPF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteRequestDTO {

	@NotNull
	@Size(max = 100)
	private String nome;

	@NotNull
	@CPF
	private String cpf;

	@NotNull
	private Long telefone;

	@NotNull
	@Digits(integer = 18, fraction = 2)
	private BigDecimal rendaMensal;

	@NotNull
	@Size(max = 100)
	private String logradouro;

	@NotNull
	private Integer numero;

	@Size(max = 100)
	private String complemento;

	@NotNull
	@Size(max = 100)
	private String bairro;

	@NotNull
	@Size(max = 100)
	private String cidade;

	@NotNull
	@Size(min = 2, max = 2)
	private String estado;

	@NotNull
	@Size(max = 8)
	private String cep;

}
