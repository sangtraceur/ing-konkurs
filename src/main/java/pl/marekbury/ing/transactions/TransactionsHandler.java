package pl.marekbury.ing.transactions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import pl.marekbury.ing.PostHandlerAdapter;


public class TransactionsHandler extends PostHandlerAdapter {

  protected void internalHandle(final HttpExchange exchange) {
    final var stream = exchange.getRequestBody();
    final TreeMap<String, AccountState> db = new TreeMap<>();
    TransactionsJsonParser.INSTANCE.streamTransactions(stream)
        .onErrorStop()
        .subscribe(t -> {
              credit(db, t.getCreditAccount(), t.getAmount());
              debit(db, t.getDebitAccount(), t.getAmount());
            }, e -> handleException(e, exchange),
            () -> {

              byte[] responseString = null;
              try {
                responseString = mapper.writeValueAsString(db.values())
                    .getBytes();
              } catch (JsonProcessingException e) {
                handleException(e, exchange);
                return;
              }

              try {
                exchange.sendResponseHeaders(200, responseString.length);
                exchange.getResponseHeaders().put("Content-Type", List.of("application/json"));
                try (final OutputStream os = exchange.getResponseBody()) {
                  os.write(responseString);
                }
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            });
  }

  private void debit(Map<String, AccountState> db, String account, BigDecimal amount) {
    getOrCreate(db, account).debit(amount);
  }

  private void credit(Map<String, AccountState> db, String account, BigDecimal amount) {
    getOrCreate(db, account).credit(amount);
  }

  private AccountState getOrCreate(Map<String, AccountState> db, String account) {
    AccountState state = db.get(account);
    if (state == null) {
      state = new AccountState(account);
      db.put(account, state);
    }
    return state;
  }

}
