package handler.game;

import com.scoreboard.domain.handler.Handler;
import com.scoreboard.domain.model.state.PointResult;
import com.scoreboard.domain.handler.game.NoAdvantageGameHandler;
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
class NoAdvantageGameHandlerTest {

    @Mock
    private Handler nextHandler;

    @Mock
    private OngoingMatch match;

    private final NoAdvantageGameHandler handler = new NoAdvantageGameHandler();
    private final TennisPlayer firstPlayer = new TennisPlayer("Player 1");

    @BeforeEach
    void setUp() {
        handler.setNextHandler(nextHandler);
    }

    @Test
    void givenPlayer_whenScorerHasForty_shouldFinishGame() {
        // given
        when(match.getPoints(firstPlayer)).thenReturn(Points.FORTY);
        when(nextHandler.handle(match, firstPlayer)).thenReturn(PointResult.GAME_FINISHED);

        // when
        PointResult result = handler.handle(match, firstPlayer);

        // then
        assertEquals(PointResult.GAME_FINISHED, result);
        verify(nextHandler).handle(match, firstPlayer);
    }

    @Test
    void givenPlayer_whenScorerHasNotForty_shouldAwardPoint() {
        // given
        when(match.getPoints(firstPlayer)).thenReturn(Points.THIRTY);

        // when
        PointResult result = handler.handle(match, firstPlayer);

        // then
        assertEquals(PointResult.POINT_AWARDED, result);
        verifyNoInteractions(nextHandler);
    }
}
