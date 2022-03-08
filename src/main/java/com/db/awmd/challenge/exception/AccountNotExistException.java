package com.db.awmd.challenge.exception;

public class AccountNotExistException extends RuntimeException {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public AccountNotExistException(String message) {
    super(message);
  }
}
