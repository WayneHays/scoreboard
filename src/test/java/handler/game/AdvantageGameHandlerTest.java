package handler.game;

import com.scoreboard.domain.handler.Handler;
import com.scoreboard.domain.model.state.PointResult;
import com.scoreboard.domain.handler.game.AdvantageGameHandler;
import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.domain.model.state.Points;
import com.scoreboard.domain.model.TennisPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdvantageGameHandlerTest {

    @Mock
    private Handler nextHandler;

    @Mock
    private OngoingMatch match;

    private final AdvantageGameHandler handler = new AdvantageGameHandler();
    private final TennisPlayer firstPlayer = new TennisPlayer("Player 1");
    private final TennisPlayer secondPlayer = new TennisPlayer("Player 2");

    @BeforeEach
    void setUp() {
        handler.setNextHandler(nextHandler);
    }

    @Test
    void givenToPlayers_whenScorerHasAdvantage_shouldFinishGame() {
        // given
        when(match.getPoints(firstPlayer)).thenReturn(Points.ADVANTAGE);
        when(match.getPoints(secondPlayer)).thenReturn(Points.FORTY);
        when(match.getOpponent(firstPlayer)).thenReturn(secondPlayer);
        when(nextHandler.handle(match, firstPlayer)).thenReturn(PointResult.GAME_FINISHED);

        // when
        PointResult result = handler.handle(match, firstPlayer);

        // then
        assertEquals(PointResult.GAME_FINISHED, result);
        verify(nextHandler).handle(match, firstPlayer);
    }

    @Test
    void givenTwoPlayers_whenOpponentHasAdvantage_shouldReturnToDeuce() {
        // given
        when(match.getPoints(firstPlayer)).thenReturn(Points.FORTY);
        when(match.getPoints(secondPlayer)).thenReturn(Points.ADVANTAGE);
        when(match.getOpponent(firstPlayer)).thenReturn(secondPlayer);

        // when
        PointResult result = handler.handle(match, firstPlayer);


        // then
        assertEquals(PointResult.DEUCE, result);
        verifyNoInteractions(nextHandler);
    }

    @Test
    void givenTwoPlayers_whenDeuce_shouldReturnAdvantage() {
        // given
        when(match.getPoints(firstPlayer)).thenReturn(Points.FORTY);
        when(match.getPoints(secondPlayer)).thenReturn(Points.FORTY);
        when(match.getOpponent(firstPlayer)).thenReturn(secondPlayer);

        // when
        PointResult result = handler.handle(match, firstPlayer);

        // then
        assertEquals(PointResult.ADVANTAGE, result);
        verifyNoInteractions(nextHandler);
    }

    @Test
    void givenTwoPlayers_whenScorerHasFortyAndOpponentLess_shouldFinishGame() {
        // given
        when(match.getPoints(firstPlayer)).thenReturn(Points.FORTY);
        when(match.getPoints(secondPlayer)).thenReturn(Points.THIRTY);
        when(match.getOpponent(firstPlayer)).thenReturn(secondPlayer);
        when(nextHandler.handle(match, firstPlayer)).thenReturn(PointResult.GAME_FINISHED);

        // when
        PointResult result = handler.handle(match, firstPlayer);

        // then
        assertEquals(PointResult.GAME_FINISHED, result);
        verify(nextHandler).handle(match, firstPlayer);
    }

    @Test
    void givenTwoPlayers_whenRegularScore_shouldAwardPoint() {
        // given
        when(match.getPoints(firstPlayer)).thenReturn(Points.ZERO);
        when(match.getPoints(secondPlayer)).thenReturn(Points.ZERO);
        when(match.getOpponent(firstPlayer)).thenReturn(secondPlayer);

        // when
        PointResult result = handler.handle(match, firstPlayer);

        // then
        assertEquals(PointResult.POINT_AWARDED, result);
        verifyNoInteractions(nextHandler);
    }
}
