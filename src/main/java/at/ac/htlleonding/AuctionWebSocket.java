package at.ac.htlleonding;

import at.ac.htlleonding.api.AuctionService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/auctionws/{username}")
@ApplicationScoped
public class AuctionWebSocket {
  @Inject
  AuctionService auctionService;

  Map<String, Session> sessions = new ConcurrentHashMap<>();

  @OnOpen
  public void onOpen(Session session, @PathParam("username") String username) {
    sessions.put(username, session);
    notifyClients("User " + username + " joined");
  }

  @OnClose
  public void onClose(Session session, @PathParam("username") String username) {
    sessions.remove(username);
    notifyClients("User " + username + " left");
  }

  @OnError
  public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
    sessions.remove(username);
    notifyClients("User " + username + " left on error: " + throwable);
  }

  @OnMessage
  public void onMessage(int bid, @PathParam("username") String username) {
    if(auctionService.makeBid(username, bid)) {
      notifyClients(username + " made the highest bid: " + bid);

      Optional<String> winner = auctionService.getWinningBidder();
      if(winner.isPresent()) {
        notifyClients(username + " won the auction");
      }
    }
  }

  private void notifyClients(String message) {
    sessions.values().forEach(s -> {
      s.getAsyncRemote().sendObject(message, result ->  {
        if (result.getException() != null) {
          System.err.println("Unable to send message: " + result.getException());
        }
      });
    });
  }

}
