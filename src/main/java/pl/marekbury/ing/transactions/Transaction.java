package pl.marekbury.ing.transactions;

import java.math.BigDecimal;

public class Transaction {


  private String debitAccount;
  private String creditAccount;
  private BigDecimal amount;

  public String getDebitAccount() {
    return debitAccount;
  }

  public String getCreditAccount() {
    return creditAccount;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setDebitAccount(final String debitAccount) {
    this.debitAccount = debitAccount;
  }

  public void setCreditAccount(final String creditAccount) {
    this.creditAccount = creditAccount;
  }

  public void setAmount(final BigDecimal amount) {
    this.amount = amount;
  }
}
