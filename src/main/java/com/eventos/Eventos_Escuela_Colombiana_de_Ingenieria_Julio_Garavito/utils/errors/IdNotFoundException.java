package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors;

public class IdNotFoundException extends RuntimeException{
	private static final String ERROR_MESSAGE = "ID de %s no encontrado";

	public IdNotFoundException(String tableName) {
		super(String.format(ERROR_MESSAGE, tableName));
	}
}
