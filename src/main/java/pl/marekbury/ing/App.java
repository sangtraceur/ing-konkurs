package pl.marekbury.ing;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import pl.marekbury.ing.atm.AtmHandler;
import pl.marekbury.ing.game.GameHandler;
import pl.marekbury.ing.transactions.TransactionsHandler;

public class App {

  public static void main(final String[] args) throws Exception {
    final HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
    server.setExecutor(null);
    server.createContext("/transactions/report", new TransactionsHandler());
    server.createContext("/onlinegame/calculate", new GameHandler());
    server.createContext("/atms/calculateOrder", new AtmHandler());
    server.start();
    System.out.println("Come on! (ง'̀-'́)ง Show me your requests!");
  }

}




