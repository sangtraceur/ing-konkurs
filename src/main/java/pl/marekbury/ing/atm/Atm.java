package pl.marekbury.ing.atm;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class Atm {


  private Integer region;
  private Integer atmId;

  public Integer getAtmId() {
    return atmId;
  }

  public Integer getRegion() {
    return region;
  }

  public void setAtmId(final Integer atmId) {
    this.atmId = atmId;
  }

  public void setRegion(final Integer region) {
    this.region = region;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Atm atm = (Atm) o;
    return atmId.equals(atm.atmId) && region.equals(atm.region);
  }

  @Override
  public int hashCode() {
    return Objects.hash(atmId, region);
  }
}
