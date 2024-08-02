package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors;

public class DuplicatedRowException extends RuntimeException {
	private static final String ERROR_MESSAGE = "El campo %s ya se encuentra en uso";

	public DuplicatedRowException(String field) {
		super(String.format(ERROR_MESSAGE, field.toLowerCase()));
	}
}
