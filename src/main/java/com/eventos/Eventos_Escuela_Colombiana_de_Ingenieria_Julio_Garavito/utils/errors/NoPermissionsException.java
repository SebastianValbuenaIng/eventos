package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors;

public class NoPermissionsException extends RuntimeException {
	public NoPermissionsException() {
		super("No tiene permisos");
	}
}
