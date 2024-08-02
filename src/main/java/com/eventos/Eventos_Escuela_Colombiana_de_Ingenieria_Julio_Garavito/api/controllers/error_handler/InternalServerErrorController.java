package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.controllers.error_handler;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.ServerErrorException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorController {

	@ExceptionHandler({ServerErrorException.class, DataIntegrityViolationException.class, NullPointerException.class})
	public BaseErrorResponse handleInternalServerError(RuntimeException exception) {
		System.out.println(exception.getMessage());
		return BaseErrorResponse.builder()
			.message("Ha ocurrido un error")
			.code(HttpStatus.INTERNAL_SERVER_ERROR.value())
			.build()
			;
	}
}
