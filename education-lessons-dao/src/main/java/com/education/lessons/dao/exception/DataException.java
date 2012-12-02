package com.education.lessons.dao.exception;

public class DataException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance without detail message.
	 */
	public DataException() {}

	/**
	 * Constructs an instance with a specified detail message.
	 */
	public DataException(String message) {
		super(message);
	}

	/**
	 * Constructs an instance with a specified cause.
	 */
	public DataException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs an instance with a specified detail message and cause.
	 */
	public DataException(String message, Throwable cause) {
		super(message, cause);
	}
}
