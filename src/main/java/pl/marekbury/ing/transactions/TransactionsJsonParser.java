package pl.marekbury.ing.transactions;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonToken;
import java.io.InputStream;
import java.math.BigDecimal;
import pl.marekbury.ing.Validator;
import reactor.core.publisher.Flux;

public enum TransactionsJsonParser {

  INSTANCE; // That's right ;) no spring di no fun, going in the old way

  private static String ERROR = "Unexpected json element";

  public Flux<Transaction> streamTransactions(final InputStream jsonStream) {
    return Flux.create(fluxSink -> {
      try {
        final var f = new JsonFactory();
        final var parser = f.createParser(jsonStream);
        if (JsonToken.START_ARRAY != parser.nextToken()) {
          fluxSink.error(new IllegalArgumentException("Array start [ expected"));
        }

        while (JsonToken.END_ARRAY != parser.nextToken()) {
          if (JsonToken.START_OBJECT != parser.currentToken()) {
            fluxSink.error(new IllegalArgumentException("Object start { expected"));
          }
          final var t = new Transaction(); //Setter are faster than builders in this case
          while (JsonToken.END_OBJECT != parser.nextToken()) {
            final String fieldName = parser.getCurrentName();
            if ("debitAccount".equals(fieldName)) {
              parser.nextToken();
              t.setDebitAccount(parser.getText());
              Validator.INSTANCE.validateLength(fluxSink, t.getDebitAccount(), 26);
            } else if ("creditAccount".equals(fieldName)) {
              parser.nextToken();
              t.setCreditAccount(parser.getText());
              Validator.INSTANCE.validateLength(fluxSink, t.getCreditAccount(), 26);
            } else if ("amount".equals(fieldName)) {
              parser.nextToken();
              t.setAmount(new BigDecimal(parser.getText()));
            }
          }
          fluxSink.next(t);
        }
        fluxSink.complete();
      } catch (final Exception e) {
        fluxSink.error(e);
      }
    });
  }
}
