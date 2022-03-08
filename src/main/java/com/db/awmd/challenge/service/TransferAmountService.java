package com.db.awmd.challenge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.constant.ErrorConstants;
import com.db.awmd.challenge.domain.MoneyTransferRequest;
import com.db.awmd.challenge.exception.AccountNotExistException;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransferAmountService {

	  @Getter
	  private final AccountsRepository accountsRepository;
	  
	  @Autowired
	  public TransferAmountService(AccountsRepository accountsRepository) {
	    this.accountsRepository = accountsRepository;
	  }
	  
	  public void transferMoney(MoneyTransferRequest moneyTransferRequest) {
		
		// check if account is exists or not
		if(!accountsRepository.isAccountExists(moneyTransferRequest.getAccountFromId()))
		{
			log.error(ErrorConstants.WRONG_FROM_ACCOUNT_ID);
			throw new AccountNotExistException(ErrorConstants.WRONG_FROM_ACCOUNT_ID);
		}
		
		if(!accountsRepository.isAccountExists(moneyTransferRequest.getAccountToId()))
		{
			log.error(ErrorConstants.WRONG_TO_ACCOUNT_ID);
			throw new AccountNotExistException(ErrorConstants.WRONG_TO_ACCOUNT_ID);
		}
		
		this.accountsRepository.transferMoney(moneyTransferRequest);
	  }
}
