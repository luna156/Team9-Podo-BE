package com.softeer.podo.event.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MaxCommentLengthException extends RuntimeException {
	private String message;
}
