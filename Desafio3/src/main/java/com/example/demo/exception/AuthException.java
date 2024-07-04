package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthException extends Exception{

		public static final long serialVersionUID = 1L;

		public AuthException() {
			super("Autenticação necessária");
		}
		
	}
