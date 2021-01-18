package at.ac.htlleonding;

import at.ac.htlleonding.api.AuctionService;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class AuctionServiceImpl implements AuctionService {
  private static final int BIDS_PER_CYCLE = 10;

  String highestBidder;
  int highestBid;
  int bidNr;

  public AuctionServiceImpl() {
    reset();
  }

  public synchronized boolean makeBid(String userName, int newBid) {
    if(newBid > highestBid && bidNr < BIDS_PER_CYCLE) {
      highestBidder = userName;
      highestBid = newBid;
      bidNr++;

      return true;
    }

    return false;
  }

  public synchronized Optional<String> getWinningBidder() {
    if(bidNr == BIDS_PER_CYCLE) {
      return Optional.of(highestBidder);
    }

    return Optional.empty();
  }

  public synchronized void reset() {
    bidNr = 0;
    highestBid = 0;
    highestBidder = null;
  }

}
