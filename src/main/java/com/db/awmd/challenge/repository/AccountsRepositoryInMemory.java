package com.db.awmd.challenge.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.constant.ErrorConstants;
import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.MoneyTransferRequest;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.LowAccountBalanceException;
import com.db.awmd.challenge.service.EmailNotificationService;
import com.db.awmd.challenge.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class AccountsRepositoryInMemory implements AccountsRepository {

	public NotificationService notificationService = new EmailNotificationService();
	
  private final Map<String, Account> accounts = new ConcurrentHashMap<>();

  @Override
  public void createAccount(Account account) throws DuplicateAccountIdException {
    Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
    if (previousAccount != null) {
      throw new DuplicateAccountIdException(
        "Account id " + account.getAccountId() + " already exists!");
    }
  }

  @Override
  public Account getAccount(String accountId) {
    return accounts.get(accountId);
  }

  @Override
  public void clearAccounts() {
    accounts.clear();
  }

	@Override
	public void transferMoney(MoneyTransferRequest moneyTransferRequest) {
		
		Account fromAccount;
		Account toAccount;
		
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		lock.readLock().lock();;	

		try {
			fromAccount = accounts.get(moneyTransferRequest.getAccountFromId());
			toAccount = accounts.get(moneyTransferRequest.getAccountToId());
		}
		finally {
			lock.readLock().unlock();
		}
			
		//check if amount to be transfer is less than account available balance
		if(-1 == fromAccount.getBalance().compareTo(moneyTransferRequest.getAmount())) {
				log.error(ErrorConstants.LOW_ACCOUNT_BALANCE);
				throw new LowAccountBalanceException(ErrorConstants.LOW_ACCOUNT_BALANCE);
		}
		
		lock.writeLock().lock();
		try {
			fromAccount.setBalance(fromAccount.getBalance().subtract(moneyTransferRequest.getAmount()));
			toAccount.setBalance(toAccount.getBalance().add(moneyTransferRequest.getAmount()));
		}
		finally {
			lock.writeLock().unlock();
		}
		log.info("Transfer success : calling Notification servive");
			
		notificationService.notifyAboutTransfer(fromAccount, moneyTransferRequest.getAmount()+ ErrorConstants.NOTIFICATION_MSG_DEBIT
					+ moneyTransferRequest.getAccountToId());
		notificationService.notifyAboutTransfer(toAccount, moneyTransferRequest.getAmount()+ ErrorConstants.NOTIFICATION_MSG_CREDIT
					+ moneyTransferRequest.getAccountFromId());
	}
	
	@Override
	public boolean isAccountExists(String accountId) {
		return accounts.containsKey(accountId);
	}

}
