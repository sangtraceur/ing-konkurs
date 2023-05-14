package pl.marekbury.ing.transactions;

import java.math.BigDecimal;

public class AccountState {

  private String account;
  private Long debitCount = 0L;
  private Long creditCount = 0L;
  private BigDecimal balance = BigDecimal.ZERO;

  AccountState(String account) {
    this.account = account;
  }

  public String getAccount() {
    return account;
  }

  public Long getDebitCount() {
    return debitCount;
  }

  public Long getCreditCount() {
    return creditCount;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void credit(final BigDecimal creditAmount) {
    creditCount++;
    balance = balance.add(creditAmount);
  }

  public void debit(final BigDecimal debitAmount) {
    debitCount++;
    balance = balance.subtract(debitAmount);
  }

}
