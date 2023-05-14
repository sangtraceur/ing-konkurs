package pl.marekbury.ing.game;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonToken;
import java.io.InputStream;
import pl.marekbury.ing.Validator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public enum GameQueueJsonParser {
  INSTANCE;

  public GameQueuePublishers streamQueue(final InputStream jsonStream) {

    final Sinks.One<Integer> groupSizeSink = Sinks.one();

    return new GameQueuePublishers(groupSizeSink.asMono(), Flux.create(sink -> {
      try {
        final var f = new JsonFactory();
        final var parser = f.createParser(jsonStream);
        if (JsonToken.START_OBJECT != parser.nextToken()) {
          sink.error(new IllegalArgumentException("Root object start { expected"));
        }

        while (!parser.isClosed()) {
          JsonToken jsonToken = parser.nextToken();

          if (JsonToken.FIELD_NAME.equals(jsonToken)) {
            String fieldName = parser.getCurrentName();

            if ("groupCount".equals(fieldName)) {
              parser.nextToken();
              groupSizeSink.emitValue(parser.getValueAsInt(), null);
            } else if ("clans".equals(fieldName)) {
              while (!JsonToken.END_ARRAY.equals(jsonToken = parser.nextToken())) {
                if (JsonToken.START_OBJECT.equals(jsonToken)) {
                  Integer players = null;
                  Integer points = null;
                  while (!JsonToken.END_OBJECT.equals(jsonToken = parser.nextToken())) {
                    if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                      String innerFieldName = parser.getCurrentName();
                      if ("numberOfPlayers".equals(innerFieldName)) {
                        parser.nextToken();
                        players = parser.getValueAsInt();
                        Validator.INSTANCE.validateInRange(sink, players, 1, 1000);
                      } else if ("points".equals(innerFieldName)) {
                        parser.nextToken();
                        points = parser.getValueAsInt();
                        Validator.INSTANCE.validateInRange(sink, points, 1, 100000);
                      }
                    }
                  }
                  sink.next(new Clan(players, points)); // Let's try record here - this immutability is ok,
                  // but in this kind of processing setters looks much better
                }
              }
            }
          }
        }
        sink.complete();
      } catch (final Exception e) {
        sink.error(e);
      }
    }));
  }


}
