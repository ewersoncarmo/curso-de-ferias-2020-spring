package com.matera.cursoferias.digitalbank.dto.response;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class ResponseDTO<T> {

	private T dado;
	private List<ErroResponseDTO> erros;

	public ResponseDTO(T dado){
		this.dado = dado;
	}

	public static ResponseDTO<Object> comErros(List<ErroResponseDTO> erros) {
		ResponseDTO<Object> retorno = new ResponseDTO<>();
		retorno.setErros(erros);

		return retorno;
	}

	public static ResponseDTO<Object> comErro(ErroResponseDTO erro) {
		return comErros(Arrays.asList(erro));
	}

}
