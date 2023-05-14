package pl.marekbury.ing;

import reactor.core.publisher.FluxSink;

public enum Validator {
  INSTANCE;

  public void validateInRange(final FluxSink<?> fluxSink, final Integer val, final int from, final int to) {
    if (val == null || val < from && val > to) {
      fluxSink.error(new IllegalArgumentException("Invalid value: " + val));
    }
  }

  public void validateLength(final FluxSink<?> fluxSink, final String val, int length) {
    if (val == null || val.length() != length){
      fluxSink.error(new IllegalArgumentException("Invalid value: " + val));
    }
  }

}
