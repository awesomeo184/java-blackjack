package blackjack.domain.player;

import blackjack.domain.card.Deck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static blackjack.domain.fixture.CardFixture.*;
import static blackjack.domain.fixture.FixedSequenceDeck.generateDeck;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Players 클래스")
class PlayersTest {

    @Test
    @DisplayName("현재 순서의 player를 반환한다")
    void testGetCurrentPlayer() {
        Deck deck = generateDeck(DUMMY_CARD, DUMMY_CARD, DUMMY_CARD, DUMMY_CARD);
        Player first = new Player("pobi", deck.initialDraw());
        Player second = new Player("jason", deck.initialDraw());
        Players players = new Players(List.of(first, second));

        assertThat(players.getCurrentTurn()).isEqualTo(first);
    }

    @Test
    @DisplayName("순서를 다음 player로 넘긴다")
    void testPassTurnToNext() {
        Deck deck = generateDeck(DUMMY_CARD, DUMMY_CARD, DUMMY_CARD, DUMMY_CARD);
        Player first = new Player("pobi", deck.initialDraw());
        Player second = new Player("jason", deck.initialDraw());
        Players players = new Players(List.of(first, second));

        players.passTurnToNext();

        assertThat(players.getCurrentTurn()).isEqualTo(second);
    }

    @Test
    @DisplayName("hit이 가능한 플레이어가 나타날 때까지 turn을 넘긴다")
    void testPassTurnUntilHittable() {
        // given
        Deck deck = generateDeck(DUMMY_CARD, DUMMY_CARD, DUMMY_CARD, DUMMY_CARD, DUMMY_CARD, DUMMY_CARD);
        Player first = new Player("1", deck.initialDraw());
        Player second = new Player("2", deck.initialDraw());
        Player third = new Player("3", deck.initialDraw());
        Players players = new Players(List.of(first, second, third));
        first.stay();
        second.stay();
        Player firstTurn = players.getCurrentTurn();

        // when
        players.passTurnUntil(Player::isAbleToHit);

        // then
        assertThat(firstTurn).isEqualTo(first);
        assertThat(players.getCurrentTurn()).isEqualTo(third);
    }

    @Test
    @DisplayName("베팅하지 않은 player가 나타날 때까지 턴을 넘긴다")
    void testPassTurnUntilBettable() {
        // given
        Deck deck = generateDeck(DUMMY_CARD, DUMMY_CARD, DUMMY_CARD, DUMMY_CARD, DUMMY_CARD, DUMMY_CARD);
        Player first = new Player("1", deck.initialDraw());
        Player second = new Player("2", deck.initialDraw());
        Player third = new Player("3", deck.initialDraw());
        Players players = new Players(List.of(first, second, third));
        first.bet(1000L);
        second.bet(1000L);
        Player firstTurn = players.getCurrentTurn();

        // when
        players.passTurnUntil(Player::isAbleToBet);

        // then
        assertThat(firstTurn).isEqualTo(first);
        assertThat(players.getCurrentTurn()).isEqualTo(third);
    }

    @Test
    @DisplayName("최대 가능 플레이어 수를 초과하면 예외를 발생시킨다")
    void throwExceptionWhenOverMaxPlayerSize() {
        Deck deck = generateDeck(DUMMY_CARD, DUMMY_CARD,
                DUMMY_CARD, DUMMY_CARD,
                DUMMY_CARD, DUMMY_CARD,
                DUMMY_CARD, DUMMY_CARD,
                DUMMY_CARD, DUMMY_CARD,
                DUMMY_CARD, DUMMY_CARD,
                DUMMY_CARD, DUMMY_CARD,
                DUMMY_CARD, DUMMY_CARD
        );
        Player first = new Player("1", deck.initialDraw());
        Player second = new Player("2", deck.initialDraw());
        Player third = new Player("3", deck.initialDraw());
        Player fourth = new Player("4", deck.initialDraw());
        Player fifth = new Player("5", deck.initialDraw());
        Player sixth = new Player("6", deck.initialDraw());
        Player seventh = new Player("7", deck.initialDraw());
        Player eighth = new Player("8", deck.initialDraw());

        assertThatThrownBy(() -> new Players(List.of(first, second, third, fourth, fifth, sixth, seventh, eighth)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("플레이어의 이름은 중복될 수 없다")
    void throwExceptionWhenNameDuplicated() {
        Deck deck = generateDeck(DUMMY_CARD, DUMMY_CARD, DUMMY_CARD, DUMMY_CARD);

        Player first = new Player("1", deck.initialDraw());
        Player second = new Player("1", deck.initialDraw());

        assertThatThrownBy(() -> new Players(List.of(first, second)))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Nested
    @DisplayName("isPossibleToPlay 메서드는")
    class DescribeIsPossibleToPlay {

        private Player bustPlayer;
        private Player stayPlayer;
        private Player playablePlayer;
        private final Deck deck = generateDeck(SPADE_JACK, SPADE_JACK, SPADE_TWO,
                DUMMY_CARD, DUMMY_CARD, DUMMY_CARD, DUMMY_CARD);

        @BeforeEach
        void setUp() {
            bustPlayer = new Player("1", deck.initialDraw());
            bustPlayer.addCard(deck.draw());

            stayPlayer = new Player("2", deck.initialDraw());
            stayPlayer.stay();

            playablePlayer = new Player("3", deck.initialDraw());
        }

        @Nested
        @DisplayName("플레이 가능한 플레이어가 남아있다면")
        class ContextWithRemainPlayablePlayer {

            @Test
            @DisplayName("참을 반환한다")
            void itReturnsTrue() {
                Players players = new Players(List.of(playablePlayer, bustPlayer));
                assertThat(players.isAllPlayerSatisfy(Player::isAbleToHit)).isTrue();
            }
        }

        @Nested
        @DisplayName("플레이 가능한 플레이어가 남아있지 않다면")
        class ContextWithNoPlayablePlayer {

            @Test
            @DisplayName("거짓을 반환한다")
            void itReturnsFalse() {
                Players players = new Players(List.of(stayPlayer, bustPlayer));
                assertThat(players.isAllPlayerSatisfy(Player::isAbleToHit)).isFalse();
            }
        }
    }
}
