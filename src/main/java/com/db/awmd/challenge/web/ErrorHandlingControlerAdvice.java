package com.db.awmd.challenge.web;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.db.awmd.challenge.constant.ErrorConstants;
import com.db.awmd.challenge.domain.ErrorResponse;
import com.db.awmd.challenge.exception.AccountNotExistException;
import com.db.awmd.challenge.exception.LowAccountBalanceException;

@ControllerAdvice
public class ErrorHandlingControlerAdvice {

	@ExceptionHandler(LowAccountBalanceException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ErrorResponse onLowBalanceValidation (LowAccountBalanceException e)
	{
		return new ErrorResponse(ErrorConstants.BAD_REQUEST_ERROR_CODE, e.getMessage(), new Date().toString());
	}
	
	@ExceptionHandler(AccountNotExistException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ErrorResponse onInvalidAcountValidation (AccountNotExistException e)
	{
		return new ErrorResponse(ErrorConstants.BAD_REQUEST_ERROR_CODE, e.getMessage(), new Date().toString());
	}
}
