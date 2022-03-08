package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.db.awmd.challenge.constant.ErrorConstants;
import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.MoneyTransferRequest;
import com.db.awmd.challenge.service.NotificationService;
import com.db.awmd.challenge.service.TransferAmountService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransferAmountControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private TransferAmountService transferAmountService;
  
  @Mock
  private NotificationService notificationService;

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
  public void transferMoney() throws Exception {
    this.mockMvc.perform(put("/v1/transferMoney").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountFromId\":\"Id-123\", \"accountToId\":\"Id-456\", \"amount\":100}")).andExpect(status().isCreated());

    Account fromAccount = transferAmountService.getAccountsRepository().getAccount("Id-123");
    assertThat(fromAccount.getAccountId()).isEqualTo("Id-123");
    assertThat(fromAccount.getBalance()).isEqualByComparingTo("900");
    
    Account toAccount = transferAmountService.getAccountsRepository().getAccount("Id-456");
    assertThat(toAccount.getAccountId()).isEqualTo("Id-456");
    assertThat(toAccount.getBalance()).isEqualByComparingTo("2100");
  }
  
  @Test
  public void transferMoneyNotificationServiceCallCheck() throws Exception {

    MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest("Id-123", "Id-456", new BigDecimal(100));
    transferAmountService.getAccountsRepository().transferMoney(moneyTransferRequest);
    Account fromAccount = transferAmountService.getAccountsRepository().getAccount("Id-123");
    
    notificationService.notifyAboutTransfer(fromAccount, 100+ErrorConstants.NOTIFICATION_MSG_DEBIT+"Id-456");
    Mockito.verify(notificationService, atLeastOnce()).notifyAboutTransfer(fromAccount, 100+ErrorConstants.NOTIFICATION_MSG_DEBIT+"Id-456");
  }
  
  @Test
  public void transferWithWrongFromAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountFromId\":\"Id-098\", \"accountToId\":\"Id-456\", \"amount\":100}")).andExpect(status().isBadRequest());
  }

  @Test
  public void transferWithWrongToAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountFromId\":\"Id-123\", \"accountToId\":\"Id-098\", \"amount\":100}")).andExpect(status().isBadRequest());
  }
  
  @Test
  public void lowAccountBalance() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountFromId\":\"Id-123\", \"accountToId\":\"Id-456\", \"amount\":10000}")).andExpect(status().isBadRequest());
  }
  
  @Test
  public void transferNoFromAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountToId\":\"Id-456\", \"amount\":100}")).andExpect(status().isBadRequest());
  }
  
  @Test
  public void transferNoToAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountFromId\":\"Id-456\", \"amount\":100}")).andExpect(status().isBadRequest());
  }
  
  @Test
  public void transferNoAmount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountFromId\":\"Id-123\", \"accountToId\":\"Id-456\"}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNoBalance() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\"}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNoBody() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNegativeBalance() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":-1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountEmptyAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"\",\"balance\":1000}")).andExpect(status().isBadRequest());
  }
  
  
}
