package pl.marekbury.ing.atm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import pl.marekbury.ing.PostHandlerAdapter;

public class AtmHandler extends PostHandlerAdapter {

  @Override
  protected void internalHandle(final HttpExchange exchange) {
    final var db = new TreeMap<Long, Atm>();
    AtmJsonParser.INSTANCE
        .streamRequests(exchange.getRequestBody())
        .onErrorStop()
        .subscribe((r -> {
              db.put(1000000000L + r.getAtm().getRegion() * 100000L + r.getRequestType() * 10000L + r.getAtm()
                  .getAtmId(), r.getAtm());
            }),
            e -> handleException(e, exchange),
            () -> {
              byte[] responseString = null;
              try {
                responseString = prepareBody(db).getBytes(StandardCharsets.UTF_8);
              } catch (JsonProcessingException e) {
                handleException(e, exchange);
              }
              try {
                try (final OutputStream os = exchange.getResponseBody()) {
                  exchange.sendResponseHeaders(200, responseString.length);
                  exchange.getResponseHeaders().put("Content-Type", List.of("application/json"));
                  os.write(responseString);
                }
              } catch (final Exception e) {
                throw new RuntimeException(e);
              }
            });
  }

  private String prepareBody(Map<Long, Atm> db) throws JsonProcessingException {
    final var set = new LinkedHashSet<Atm>();
    set.addAll(db.values());
    return mapper.writeValueAsString(set);
  }

}