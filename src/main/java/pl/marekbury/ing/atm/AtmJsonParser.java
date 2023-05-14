package pl.marekbury.ing.atm;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonToken;
import java.io.InputStream;
import pl.marekbury.ing.Validator;
import reactor.core.publisher.Flux;

public enum AtmJsonParser {
  INSTANCE;

  public Flux<AtmRequest> streamRequests(final InputStream jsonStream) {
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
          final var request = new AtmRequest(new Atm());
          while (JsonToken.END_OBJECT != parser.nextToken()) {
            final var fieldName = parser.getCurrentName();
            if ("region".equals(fieldName)) {
              parser.nextToken();
              request.getAtm().setRegion(parser.getIntValue());
              Validator.INSTANCE.validateInRange(fluxSink, request.getAtm().getRegion(), 1, 9999);
            } else if ("requestType".equals(fieldName)) {
              parser.nextToken();
              request.setRequestType(mapRegion(parser.getText()));
            } else if ("atmId".equals(fieldName)) {
              parser.nextToken();
              request.getAtm().setAtmId(parser.getIntValue());
              Validator.INSTANCE.validateInRange(fluxSink, request.getAtm().getAtmId(), 1, 9999);
            }
          }
          fluxSink.next(request);
        }
        fluxSink.complete();
      } catch (final Exception e) {
        fluxSink.error(e);
      }
    });
  }


  private Integer mapRegion(final String region) {
    //lets change region into number, to make sort much faster
    if (region == null) {
      throw new IllegalArgumentException("Region must not be null");
    }
    switch (region) {
      case "FAILURE_RESTART":
        return 1;
      case "PRIORITY":
        return 2;
      case "SIGNAL_LOW":
        return 3;
      case "STANDARD":
        return 4;

      default:
        throw new IllegalArgumentException("Unknown requestType: " + region);
    }
  }
}
