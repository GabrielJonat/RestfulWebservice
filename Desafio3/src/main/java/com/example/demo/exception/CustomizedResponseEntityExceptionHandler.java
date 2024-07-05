package com.example.demo.exception;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler{
	
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ExceptionResponse> handleAll(Exception err, WebRequest req){
		
		ExceptionResponse er = new ExceptionResponse(new Date(), err.getMessage(), req.getDescription(false));
		
		return new ResponseEntity<>(er, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(UnsuportedOp.class)
	public final ResponseEntity<ExceptionResponse> handleBadOp(Exception err, WebRequest req){
		
		ExceptionResponse er = new ExceptionResponse(new Date(), err.getMessage(), req.getDescription(false));
		
		return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
		
	}
	
	@ExceptionHandler(ResourceNotFound.class)
	public final ResponseEntity<ExceptionResponse> handleNotFound(Exception err, WebRequest req){
		
		ExceptionResponse er = new ExceptionResponse(new Date(), err.getMessage(), req.getDescription(false));
		
		return new ResponseEntity<>(er, HttpStatus.NOT_FOUND);
		
	}
	
	@ExceptionHandler(AuthException.class)
	public final ResponseEntity<ExceptionResponse> handleAuth(Exception err, WebRequest req){
		
		ExceptionResponse er = new ExceptionResponse(new Date(), err.getMessage(), req.getDescription(false));
		
		return new ResponseEntity<>(er, HttpStatus.UNAUTHORIZED);
		
	}

}
