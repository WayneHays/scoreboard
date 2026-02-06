package handler.tiebreak;

import com.scoreboard.domain.handler.Handler;
import com.scoreboard.domain.model.state.PointResult;
import com.scoreboard.domain.handler.tiebreak.TiebreakHandler;
import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.domain.model.TennisPlayer;
import com.scoreboard.domain.rules.TiebreakRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TiebreakHandlerTest {

    @Mock
    private Handler nextHandler;

    @Mock
    private TiebreakRules rules;

    @Mock
    private OngoingMatch match;

    private TiebreakHandler handler;
    private final TennisPlayer firstPlayer = new TennisPlayer("Player 1");
    private final TennisPlayer secondPlayer = new TennisPlayer("Player 2");

    @BeforeEach
    void setUp() {
        handler = new TiebreakHandler(rules);
        handler.setNextHandler(nextHandler);
    }

    @Test
    void givenTwoPlayers_whenTiebreakOver_shouldFinishSet() {
        // given
        when(match.getTieBreakPoints(firstPlayer)).thenReturn(6);
        when(match.getOpponent(firstPlayer)).thenReturn(secondPlayer);
        when(match.getTieBreakPoints(secondPlayer)).thenReturn(5);
        when(nextHandler.handle(match, firstPlayer)).thenReturn(PointResult.SET_FINISHED);

        // when
        PointResult result = handler.handle(match, firstPlayer);

        // then
        assertEquals(PointResult.SET_FINISHED, result);
    }

    @Test
    void givenTwoPlayers_whenTiebreakContinues_shouldContinue() {
        // given
        when(match.getTieBreakPoints(firstPlayer)).thenReturn(5);
        when(match.getOpponent(firstPlayer)).thenReturn(secondPlayer);
        when(match.getTieBreakPoints(secondPlayer)).thenReturn(5);

        // when
        PointResult result = handler.handle(match, firstPlayer);

        // then
        assertEquals(PointResult.TIE_BREAK_POINT_AWARDED, result);
    }
}
