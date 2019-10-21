package com.matera.cursoferias.digitalbank.dto.response;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class ResponseDTO<T> {

	private T dado;
	private List<ErroResponseDTO> erros;

	public ResponseDTO() {}
	
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
	
	public T getDado() {
		return dado;
	}

	public void setDado(T dado) {
		this.dado = dado;
	}

	public List<ErroResponseDTO> getErros() {
		return erros;
	}

	public void setErros(List<ErroResponseDTO> erros) {
		this.erros = erros;
	}
	
}
