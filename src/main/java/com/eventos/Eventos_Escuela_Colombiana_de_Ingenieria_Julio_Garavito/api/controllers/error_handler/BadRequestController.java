package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.api.controllers.error_handler;

import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.BasicBadRequestException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.DuplicatedRowException;
import com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.errors.MessageBadRequestException;
import jakarta.validation.UnexpectedTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestController {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public BaseErrorResponse handleNotValidArgument(MethodArgumentNotValidException exception) {
		System.out.println(exception.getAllErrors().get(0).getDefaultMessage());
		return BaseErrorResponse.builder()
			.message(exception.getAllErrors().get(0).getDefaultMessage())
			.code(HttpStatus.BAD_REQUEST.value())
			.build()
			;
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public BaseErrorResponse handleRequestParameterNull(MissingServletRequestParameterException exception) {
		System.out.println(exception.getMessage());
		return BaseErrorResponse.builder()
			.message("Por favor envíe los parámetros requeridos")
			.code(HttpStatus.BAD_REQUEST.value())
			.build()
			;
	}

	@ExceptionHandler({
		MethodArgumentTypeMismatchException.class,
		HttpMessageNotReadableException.class,
	})
	public BaseErrorResponse handleCustomBadRequest(RuntimeException exception) {
		System.out.println(exception.getMessage());
		return BaseErrorResponse.builder()
			.message("Ha ocurrido un error")
			.code(HttpStatus.BAD_REQUEST.value())
			.build()
			;
	}

	@ExceptionHandler({
		DuplicatedRowException.class,
		BasicBadRequestException.class,
		MessageBadRequestException.class,
	})
	public BaseErrorResponse handleBadRequest(RuntimeException exception) {
		return BaseErrorResponse.builder()
			.message(exception.getMessage())
			.code(HttpStatus.BAD_REQUEST.value())
			.build()
			;
	}

	@ExceptionHandler(UnexpectedTypeException.class)
	public BaseErrorResponse handleValidationsBadRequest(UnexpectedTypeException exception) {
		return BaseErrorResponse.builder()
			.message("No se ha podido realizar la validación")
			.code(HttpStatus.BAD_REQUEST.value())
			.build()
			;
	}

}
