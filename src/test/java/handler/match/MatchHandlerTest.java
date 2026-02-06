package handler.match;

import com.scoreboard.domain.handler.Handler;
import com.scoreboard.domain.model.state.PointResult;
import com.scoreboard.domain.handler.match.MatchHandler;
import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.domain.model.TennisPlayer;
import com.scoreboard.domain.rules.MatchRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchHandlerTest {

    @Mock
    private Handler nextHandler;

    @Mock
    private MatchRules rules;

    @Mock
    private OngoingMatch match;

    private MatchHandler handler;
    private final TennisPlayer firstPlayer = new TennisPlayer("Player 1");

    @BeforeEach
    void setUp() {
        handler = new MatchHandler(rules);
        handler.setNextHandler(nextHandler);
    }

    @Test
    void givenPlayer_whenMatchFinished_shouldFinishMatch() {
        // given
        when(match.getSets(firstPlayer)).thenReturn(1);
        when(rules.setsToWin()).thenReturn(2);

        // when
        PointResult result = handler.handle(match, firstPlayer);

        // then
        assertEquals(PointResult.MATCH_FINISHED, result);
    }

    @Test
    void givenPlayer_whenMatchNotFinished_shouldFinishSet() {
        // given
        when(match.getSets(firstPlayer)).thenReturn(0);
        when(rules.setsToWin()).thenReturn(2);

        // when
        PointResult result = handler.handle(match, firstPlayer);

        // then
        assertEquals(PointResult.SET_FINISHED, result);
    }
}
