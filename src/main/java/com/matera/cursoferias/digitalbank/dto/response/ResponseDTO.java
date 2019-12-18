package com.matera.cursoferias.digitalbank.dto.response;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class ResponseDTO<T> {

	private T dados;
	private List<ErroResponseDTO> erros;

	public ResponseDTO(T dados){
		this.dados = dados;
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
