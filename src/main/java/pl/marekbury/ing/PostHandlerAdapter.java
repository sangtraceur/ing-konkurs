package pl.marekbury.ing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public abstract class PostHandlerAdapter implements HttpHandler {

  static {
    mapper = new ObjectMapper();
  }

  protected static final ObjectMapper mapper;

  @Override
  public void handle(final HttpExchange exchange) throws IOException {
    if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
      exchange.sendResponseHeaders(405, 0);
      exchange.close();
      return;
    }
    internalHandle(exchange);
  }

  protected abstract void internalHandle(final HttpExchange exchange);

  protected void handleException(final Throwable e, final HttpExchange exchange) {
    final var message = e.getMessage().getBytes();
    try (final OutputStream os = exchange.getResponseBody()) {
      exchange.sendResponseHeaders(400, message.length);
      exchange.getResponseHeaders().put("Content-Type", List.of("application/json"));
      os.write(message);
    } catch (final Exception e2) {
      throw new RuntimeException(e2);
    }
  }
}
