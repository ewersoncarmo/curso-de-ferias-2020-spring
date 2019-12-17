package com.matera.cursoferias.digitalbank.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class ErroResponseDTO {

	private String campo;
	private String mensagem;

	public ErroResponseDTO(String mensagem) {
		this.mensagem = mensagem;
	}

}
