package com.db.awmd.challenge.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.MoneyTransferRequest;
import com.db.awmd.challenge.exception.AccountNotExistException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.LowAccountBalanceException;
import com.db.awmd.challenge.service.TransferAmountService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/transferMoney")
@Slf4j
public class TransferAmountController {
	
	  private final TransferAmountService transferAmountService;

	  @Autowired
	  public TransferAmountController(TransferAmountService transferAmountService) {
	    this.transferAmountService = transferAmountService;
	  }
	
	 @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<Object> transferMoney(@RequestBody @Valid MoneyTransferRequest moneyTransferRequest) {
	    log.info("Transfering {} amount from account id : {} to account id {}", moneyTransferRequest.getAmount(), 
	    		moneyTransferRequest.getAccountFromId(), moneyTransferRequest.getAccountToId());

	    try
	    {
	    	this.transferAmountService.transferMoney(moneyTransferRequest);
	    }
	    catch(AccountNotExistException anee)
	    {
	    	return new ResponseEntity<>(anee.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
	    catch(LowAccountBalanceException labe)
	    {
	    	return new ResponseEntity<>(labe.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
		}  

	    return new ResponseEntity<>(HttpStatus.CREATED);
	  }
}
