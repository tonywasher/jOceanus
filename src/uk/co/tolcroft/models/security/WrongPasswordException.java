package uk.co.tolcroft.models.security;

import uk.co.tolcroft.models.Exception;

public class WrongPasswordException extends Exception {
	/* Serial id */
	private static final long serialVersionUID = 2437428363387806213L;

	/**
	 * Constructor
	 * @param s exception string
	 */
	public WrongPasswordException(String s) { super(ExceptionClass.VALIDATE, s); } 
}
