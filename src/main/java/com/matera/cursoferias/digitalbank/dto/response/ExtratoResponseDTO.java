package com.matera.cursoferias.digitalbank.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtratoResponseDTO {

	private ContaResponseDTO conta;
	private List<ComprovanteResponseDTO> lancamentos;

}
