package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.TransferAmountService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransferAmountConcurancyTest {

  private MockMvc mockMvc;

  @Autowired
  private TransferAmountService transferAmountService;
  
  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void prepareMockMvc() {
    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

    // Reset the existing accounts before each test.
    transferAmountService.getAccountsRepository().clearAccounts();
    // create accounts before each test.
    transferAmountService.getAccountsRepository().createAccount(new Account("Id-123", new BigDecimal("1000")));
    transferAmountService.getAccountsRepository().createAccount(new Account("Id-456", new BigDecimal("2000")));
  }

  
  @Test
  public void transferMoneyConcurancyCheck() throws Exception {
	  
	  new Thread(()-> {
		  try {
			this.mockMvc.perform(put("/v1/transferMoney").contentType(MediaType.APPLICATION_JSON)
				      .content("{\"accountFromId\":\"Id-123\", \"accountToId\":\"Id-456\", \"amount\":100}")).andExpect(status().isCreated());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Account fromAccount = transferAmountService.getAccountsRepository().getAccount("Id-123");
	    assertThat(fromAccount.getAccountId()).isEqualTo("Id-123");
	    assertThat(fromAccount.getBalance()).isEqualByComparingTo("900");
	    
	  }).start();
	  
	  new Thread(()-> {
		  try {
			this.mockMvc.perform(put("/v1/transferMoney").contentType(MediaType.APPLICATION_JSON)
				      .content("{\"accountFromId\":\"Id-123\", \"accountToId\":\"Id-456\", \"amount\":100}")).andExpect(status().isCreated());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Account fromAccount = transferAmountService.getAccountsRepository().getAccount("Id-123");
	    assertThat(fromAccount.getAccountId()).isEqualTo("Id-123");
	    assertThat(fromAccount.getBalance()).isEqualByComparingTo("800");
	    
	  }).start();
	  
	  ExecutorService executorService = Executors.newFixedThreadPool(5);
	  executorService.execute(()-> {
		  try {
			this.mockMvc.perform(put("/v1/transferMoney").contentType(MediaType.APPLICATION_JSON)
				      .content("{\"accountFromId\":\"Id-123\", \"accountToId\":\"Id-456\", \"amount\":100}")).andExpect(status().isCreated());
			
			Account fromAccount = transferAmountService.getAccountsRepository().getAccount("Id-123");
		    assertThat(fromAccount.getAccountId()).isEqualTo("Id-123");
		    assertThat(fromAccount.getBalance()).isEqualByComparingTo("900");
		    
		    Account toAccount = transferAmountService.getAccountsRepository().getAccount("Id-456");
		    assertThat(toAccount.getAccountId()).isEqualTo("Id-456");
		    assertThat(toAccount.getBalance()).isEqualByComparingTo("2100");
		  } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	  });
	  
	  executorService.execute(()-> {
		  try {
			this.mockMvc.perform(put("/v1/transferMoney").contentType(MediaType.APPLICATION_JSON)
				      .content("{\"accountFromId\":\"Id-123\", \"accountToId\":\"Id-456\", \"amount\":100}")).andExpect(status().isCreated());
			Account fromAccount = transferAmountService.getAccountsRepository().getAccount("Id-123");
		    assertThat(fromAccount.getAccountId()).isEqualTo("Id-123");
		    assertThat(fromAccount.getBalance()).isEqualByComparingTo("1000");
		    
		    Account toAccount = transferAmountService.getAccountsRepository().getAccount("Id-456");
		    assertThat(toAccount.getAccountId()).isEqualTo("Id-456");
		    assertThat(toAccount.getBalance()).isEqualByComparingTo("2000");
		  
		  } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	  });
	  
    
  }
  
}
