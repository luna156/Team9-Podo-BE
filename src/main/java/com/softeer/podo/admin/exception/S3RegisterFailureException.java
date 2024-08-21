package com.softeer.podo.admin.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class S3RegisterFailureException extends RuntimeException {
	private String message;
}
