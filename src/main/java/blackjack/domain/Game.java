package blackjack.domain;

import blackjack.domain.card.RandomDeck;
import blackjack.domain.player.*;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class Game {

    private final RandomDeck deck;
    private final Players players;
    private final Dealer dealer;

    public Game(List<String> names) {
        deck = new RandomDeck();
        players = generatePlayers(names);
        dealer = new Dealer(deck.initialDraw());
    }

    private Players generatePlayers(List<String> names) {
        return new Players(names.stream()
                .map(name -> new Player(name, deck.initialDraw()))
                .collect(toList()));
    }

    public void playTurn(Command command) {
        Player currentTurn = players.getCurrentTurn();
        if (command.equals(Command.HIT)) {
            currentTurn.addCard(deck.draw());
        }
        if (command.equals(Command.STAY)) {
            currentTurn.stay();
        }
    }

    public boolean isPossibleToPlay() {
        return players.isPossibleToPlay();
    }

    public String getCurrentHitablePlayerName() {
        players.passTurnUntilHitable();
        return players.getCurrentTurn().getName();
    }

    public boolean dealerCanDraw() {
        return dealer.canDraw();
    }

    public void doDealerDraw() {
        dealer.addCard(deck.draw());
    }

    public Dealer getDealer() {
        return dealer;
    }

    public Map<String, String> getPlayerResults() {
        return players.calculateResult(dealer).entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getValue().getMessage()));
    }

    public List<String> getDealerResult() {
        return players.calculateResult(dealer).values().stream()
                .map(Outcome::reverse)
                .map(Outcome::getMessage)
                .collect(toList());
    }

    public List<Player> getPlayers() {
        return players.toList();
    }

    public Player getCurrentPlayer() {
        return players.getCurrentTurn();
    }
}
