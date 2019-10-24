package com.matera.cursoferias.digitalbank.dto.response;

import java.util.List;

public class ExtratoResponseDTO {

	private ContaResponseDTO conta;
	private List<ComprovanteResponseDTO> lancamentos;
	
	public ContaResponseDTO getConta() {
		return conta;
	}

	public void setConta(ContaResponseDTO conta) {
		this.conta = conta;
	}

	public List<ComprovanteResponseDTO> getLancamentos() {
		return lancamentos;
	}

	public void setLancamentos(List<ComprovanteResponseDTO> lancamentos) {
		this.lancamentos = lancamentos;
	}

}
