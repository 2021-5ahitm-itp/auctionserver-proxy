package at.ac.htlleonding.api;

import java.util.Optional;

public interface AuctionService {
    public boolean makeBid(String userName, int newBid);

    public Optional<String> getWinningBidder();

    public void reset();
}
