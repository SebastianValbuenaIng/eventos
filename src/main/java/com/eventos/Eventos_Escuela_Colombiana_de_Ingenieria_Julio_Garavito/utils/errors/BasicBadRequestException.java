package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors;

public class BasicBadRequestException extends RuntimeException {
	public BasicBadRequestException() {
		super("Solicitud no válida");
	}
}
