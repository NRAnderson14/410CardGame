import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Player {
    private List<Card> cards;
    private int wins;

    public Player(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
        wins = 0;
    }

    public int getWins() {
        return wins;
    }

    public void addWin() {
        ++wins;
    }

    public void setCards(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
    }

    public List<Card> getCards() {
        return cards;
    }

    public void removeCard(Card cardToBeRemoved) {
        cards.remove(cardToBeRemoved);
    }
}
