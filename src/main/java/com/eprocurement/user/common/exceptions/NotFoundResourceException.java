package com.eprocurement.user.common.exceptions;

public class NotFoundResourceException extends RuntimeException{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public NotFoundResourceException(String message) {
		super(message);
	}

	public NotFoundResourceException(String message, Throwable cause) {
		super(message,cause);
	}

}
