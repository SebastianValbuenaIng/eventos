package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors;

public class ServerErrorException extends RuntimeException {
	public ServerErrorException() {
		super("Error del servidor");
	}
}
