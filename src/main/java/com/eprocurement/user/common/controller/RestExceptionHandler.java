package com.eprocurement.user.common.controller;

import com.eprocurement.user.common.dto.ApiResponse;
import com.eprocurement.user.common.exceptions.DuplicateResourceException;
import com.eprocurement.user.common.exceptions.InvalidResourceException;
import com.eprocurement.user.common.exceptions.NotFoundResourceException;
import com.eprocurement.user.users.exceptions.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<String>> handleValidationError(MethodArgumentNotValidException ex){
		 List<String> errors = ex.getBindingResult()
		            .getFieldErrors()
		            .stream()
		            .map(error -> error.getField() + ": " + error.getDefaultMessage())
		            .toList();
		ApiResponse<String> response = ApiResponse.<String>builder().data(null)
				.message("Validation Error")
				.status(HttpStatus.BAD_REQUEST.value())
				.errorList(errors).build();
		
		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<String>> handleNotReadableException(HttpMessageNotReadableException ex){
		List<String> errors = Collections.singletonList(ex.getMessage());
		ApiResponse<String> response = ApiResponse.<String>builder().data(null)
				.message("Validation Error")
				.status(HttpStatus.BAD_REQUEST.value())
				.errorList(errors).build();

		return ResponseEntity.badRequest().body(response);
	}
	
	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ApiResponse<String>> handleDuplicateRsource(DuplicateResourceException ex){
		ArrayList<String> errorList = new ArrayList<String>();
		errorList.add(ex.getMessage());
		ApiResponse<String> response = ApiResponse.<String>builder()
				.status(HttpStatus.CONFLICT.value())
				.data(null)
				.errorList(errorList)
				.message(ex.getMessage())
				.build();

		return new ResponseEntity<>(response, HttpStatusCode.valueOf(409));

	}

	@ExceptionHandler(NotFoundResourceException.class)
	public ResponseEntity<ApiResponse<String>> handleNotFoundResource(NotFoundResourceException ex){
		ArrayList<String> errorList = new ArrayList<String>();
		errorList.add(ex.getMessage());
		ApiResponse<String> response = ApiResponse.<String>builder()
				.status(HttpStatus.CONFLICT.value())
				.data(null)
				.errorList(errorList)
				.message(ex.getMessage())
				.build();

		return new ResponseEntity<>(response, HttpStatusCode.valueOf(409));

	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<String>> handleInvalidArgument(IllegalArgumentException ex){
		ArrayList<String> errorList = new ArrayList<String>();
		errorList.add(ex.getMessage());
		ApiResponse<String> response = ApiResponse.<String>builder()
				.status(HttpStatus.BAD_REQUEST.value())
				.data(null)
				.errorList(errorList)
				.message(ex.getMessage())
				.build();
		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ApiResponse<String>> handleUserNotFound(UsernameNotFoundException ex){
		ArrayList<String> errorList = new ArrayList<String>();
		errorList.add(ex.getMessage());
		ApiResponse<String> response = ApiResponse.<String>builder()
				.status(HttpStatus.BAD_REQUEST.value())
				.data(null)
				.errorList(errorList)
				.message(ex.getMessage())
				.build();

		return ResponseEntity.badRequest().body(response);

	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ApiResponse<String>> handleUserExists(UserAlreadyExistsException ex){
		ArrayList<String> errorList = new ArrayList<String>();
		errorList.add(ex.getMessage());
		ApiResponse<String> response = ApiResponse.<String>builder()
				.status(HttpStatus.BAD_REQUEST.value())
				.data(null)
				.errorList(errorList)
				.message(ex.getMessage())
				.build();

		return ResponseEntity.badRequest().body(response);

	}

	@ExceptionHandler(InvalidResourceException.class)
	public ResponseEntity<ApiResponse<String>> handleInvalidResourceException(InvalidResourceException ex){
		ArrayList<String> errorList = new ArrayList<String>();
		errorList.add(ex.getMessage());
		ApiResponse<String> response = ApiResponse.<String>builder()
				.status(HttpStatus.CONFLICT.value())
				.data(null)
				.errorList(errorList)
				.message(ex.getMessage())
				.build();

		return new ResponseEntity<>(response, HttpStatusCode.valueOf(409));

	}

}
