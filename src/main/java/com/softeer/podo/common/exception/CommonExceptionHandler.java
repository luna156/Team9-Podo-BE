package com.softeer.podo.common.exception;

import com.softeer.podo.common.response.CommonResponse;
import com.softeer.podo.common.response.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public CommonResponse<?> validationFailedException(MethodArgumentNotValidException e, HttpServletRequest request) {
		log.warn("COMMON-001> 요청 URI: " + request.getRequestURI() + ", 에러 메세지: " + e.getMessage());
		Map<String, String> errors = new HashMap<>();
		e.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return new CommonResponse<>(ErrorCode.VALID_ERROR, errors);
	}

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public CommonResponse<?> constraintViolationException(ValidationException e, HttpServletRequest request) {
		log.warn("COMMON-002> 요청 URI: " + request.getRequestURI() + ", 에러 메세지: " + e.getMessage());
		return new CommonResponse<>(ErrorCode.VALID_ERROR, e.getMessage());
	}
}
