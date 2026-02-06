package handler.set;

import com.scoreboard.domain.handler.Handler;
import com.scoreboard.domain.model.state.PointResult;
import com.scoreboard.domain.handler.set.SetHandler;
import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.domain.model.TennisPlayer;
import com.scoreboard.domain.rules.SetRules;
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
class SetHandlerTest {

    @Mock
    private Handler nextHandler;

    @Mock
    private SetRules rules;

    @Mock
    private OngoingMatch match;

    private SetHandler handler;
    private final TennisPlayer firstPlayer = new TennisPlayer("Player 1");
    private final TennisPlayer secondPlayer = new TennisPlayer("Player 2");

    @BeforeEach
    void setUp() {
        handler = new SetHandler(rules);
        handler.setNextHandler(nextHandler);
    }

    @Test
    void givenTwoPlayers_whenSetIsOver_shouldAwardSet() {
        // given
        when(match.getGames(firstPlayer)).thenReturn(6);
        when(match.getOpponent(firstPlayer)).thenReturn(secondPlayer);
        when(match.getGames(secondPlayer)).thenReturn(4);
        when(rules.gamesToWin()).thenReturn(6);
        when(nextHandler.handle(match, firstPlayer)).thenReturn(PointResult.SET_FINISHED);

        // when
        PointResult result = handler.handle(match, firstPlayer);

        // then
        assertEquals(PointResult.SET_FINISHED, result);
        verify(nextHandler).handle(match, firstPlayer);
    }

    @Test
    void givenTwoPlayers_whenTiebreakStarted_shouldStartTiebreak() {
        // given
        when(match.getGames(firstPlayer)).thenReturn(5);
        when(match.getOpponent(firstPlayer)).thenReturn(secondPlayer);
        when(match.getGames(secondPlayer)).thenReturn(6);
        when(rules.gamesToWin()).thenReturn(6);

        // when
        PointResult result = handler.handle(match, firstPlayer);

        // then
        assertEquals(PointResult.TIE_BREAK_STARTED, result);
        verifyNoInteractions(nextHandler);
    }

    @Test
    void givenTwoPlayers_whenGameFinished_shouldFinishGame() {
        // given
        when(match.getGames(firstPlayer)).thenReturn(6);
        when(match.getOpponent(firstPlayer)).thenReturn(secondPlayer);
        when(match.getGames(secondPlayer)).thenReturn(3);
        when(rules.gamesToWin()).thenReturn(6);
        when(nextHandler.handle(match, firstPlayer)).thenReturn(PointResult.GAME_FINISHED);

        // when
        PointResult result = handler.handle(match, firstPlayer);

        // then
        assertEquals(PointResult.GAME_FINISHED, result);
    }
}
