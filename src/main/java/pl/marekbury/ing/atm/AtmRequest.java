package pl.marekbury.ing.atm;

public class AtmRequest {

  private Integer requestType;
  private Atm atm;

  public AtmRequest(Atm atm) {
    this.atm = atm;
  }

  public Atm getAtm() {
    return atm;
  }

  public Integer getRequestType() {
    return requestType;
  }

  public AtmRequest setRequestType(final Integer requestType) {
    this.requestType = requestType;
    return this;
  }

}
