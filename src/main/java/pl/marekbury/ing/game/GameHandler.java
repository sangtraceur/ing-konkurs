package pl.marekbury.ing.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import pl.marekbury.ing.PostHandlerAdapter;

public class GameHandler extends PostHandlerAdapter {

  protected void internalHandle(final HttpExchange exchange) {

    final var publishers = GameQueueJsonParser.INSTANCE.streamQueue(exchange.getRequestBody());
    final Map<Long, Clan> db = new TreeMap<>(Collections.reverseOrder());
    publishers.clans()
        .onErrorStop()
        .subscribe(
            (clan) -> {
              // dual level sorting magic is here, lame, but works fine
              db.put(100000000000L + clan.points() * 10000L + (1000L - clan.numberOfPlayers()), clan);
            },
            (e) -> handleException(e, exchange),
            () -> {
              publishers.groupCount().subscribe(groupCount -> {
                byte[] responseString = null;
                try {
                  responseString = GameHandler.mapper.writeValueAsString(buildGroups(db, groupCount))
                      .getBytes();
                } catch (JsonProcessingException e) {
                  handleException(e, exchange);
                }
                try (final OutputStream os = exchange.getResponseBody()) {
                  exchange.getResponseHeaders().put("Content-Type", List.of("application/json"));
                  exchange.sendResponseHeaders(200, responseString.length);
                  os.write(responseString);
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              });
            });
  }

  private List<List<Clan>> buildGroups(final Map<Long, Clan> db, final Integer groupCount) {

    final var groupList = new LinkedList();
    var currentGroup = new LinkedList();

    var it = db.values().iterator();
    int left = groupCount;
    while (it.hasNext()) {
      var current = it.next();

      if (left == 0) {
        groupList.add(currentGroup);
        currentGroup = new LinkedList();
        left = groupCount;
      } else if (left < current.numberOfPlayers()) {
        // search the rest
        while (it.hasNext()) {
          var currentToCheck = it.next();
          if (currentToCheck.numberOfPlayers() <= left) {
            currentGroup.add(currentToCheck);
            left -= currentToCheck.numberOfPlayers();
            it.remove();
            if (left == 0) {
              break;
            }
          }
        }
        groupList.add(currentGroup);
        currentGroup = new LinkedList();
        left = groupCount;
        it = db.values().iterator();
        current = it.next();
      }
      currentGroup.add(current);
      left -= current.numberOfPlayers();
      it.remove();
    }
    groupList.add(currentGroup);
    return groupList;
  }
}
