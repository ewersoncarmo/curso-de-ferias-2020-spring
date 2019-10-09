package com.matera.cursoferias.digitalbank.dto.request;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.br.CPF;

public class ClienteRequestDTO {

	@NotNull(message = "Nome é de preenchimento obrigatório.")
	@NotBlank(message = "Nome é de preenchimento obrigatório.")
	@Size(max = 100, message = "Nome não pode ultrapassar 100 caracteres.")
	private String nome;
	
	@NotNull(message = "CPF é de preenchimento obrigatório.")
	@CPF(message = "CPF inválido.")
	private String cpf;

	@NotNull(message = "Telefone é de preenchimento obrigatório.")
	private Long telefone;
	
	@NotNull(message = "Renda Mensal é de preenchimento obrigatório.")
	@Digits(integer = 18, fraction = 2, message = "Renda Mensal tem um valor inválido.")
	private BigDecimal rendaMensal;
	
	@NotNull(message = "Logradouro é de preenchimento obrigatório.")
	@NotBlank(message = "Logradouro é de preenchimento obrigatório.")
	@Size(max = 100, message = "Logradouro não pode ultrapassar 100 caracteres.")
	private String logradouro;
	
	@NotNull(message = "Número é de preenchimento obrigatório.")
	private Integer numero;
	
	@Size(max = 100, message = "Complemento não pode ultrapassar 100 caracteres.")
	private String complemento;
	
	@NotNull(message = "Bairro é de preenchimento obrigatório.")
	@NotBlank(message = "Bairro é de preenchimento obrigatório.")
	@Size(max = 100, message = "Bairro não pode ultrapassar 100 caracteres.")
	private String bairro;
	
	@NotNull(message = "Cidade é de preenchimento obrigatório.")
	@NotBlank(message = "Cidade é de preenchimento obrigatório.")
	@Size(max = 100, message = "Cidade não pode ultrapassar 100 caracteres.")
	private String cidade;
	
	@NotNull(message = "CEP é de preenchimento obrigatório.")
	@NotBlank(message = "CEP é de preenchimento obrigatório.")
	@Size(max = 8, message = "CEP não pode ultrapassar 8 caracteres.")
	private String cep;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Long getTelefone() {
		return telefone;
	}

	public void setTelefone(Long telefone) {
		this.telefone = telefone;
	}

	public BigDecimal getRendaMensal() {
		return rendaMensal;
	}

	public void setRendaMensal(BigDecimal rendaMensal) {
		this.rendaMensal = rendaMensal;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}
	
}
