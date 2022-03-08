package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MoneyTransferRequest {
	
	@NotNull
	@NotEmpty
	private final String accountFromId;

	@NotNull
	@NotEmpty
	private final String accountToId;
	
	@NotNull
	@Min(value = 1, message = "Amount to transfer must be positive.")
	private BigDecimal amount;
	
	public MoneyTransferRequest( String accountFromId, String accountToId) {
	    this.accountFromId = accountFromId;
	    this.accountToId = accountToId;
	}
	
	@JsonCreator
	public MoneyTransferRequest(@JsonProperty("accountFromId") String accountFromId,
			@JsonProperty("accountToId") String accountToId,
	    	@JsonProperty("balance") BigDecimal amount) {
	    this.accountFromId = accountFromId;
	    this.accountToId = accountToId;
	    this.amount = amount;
	}
}
