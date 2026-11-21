package com.eprocurement.user.common.exceptions;

public class DuplicateResourceException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateResourceException(String message) {
		super(message);
	}
	
	public DuplicateResourceException(String message,Throwable cause) {
		super(message,cause);
	}

}
