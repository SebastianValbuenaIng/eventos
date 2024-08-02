package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.controllers.error_handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class BaseErrorResponse {
	private Integer code;
	private String message;
}
