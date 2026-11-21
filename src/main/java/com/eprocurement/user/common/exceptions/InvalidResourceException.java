package com.eprocurement.user.common.exceptions;

public class InvalidResourceException extends RuntimeException{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public InvalidResourceException(String message) {
		super(message);
	}

	public InvalidResourceException(String message, Throwable cause) {
		super(message,cause);
	}

}
