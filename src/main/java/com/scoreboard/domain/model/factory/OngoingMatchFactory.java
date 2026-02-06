package com.scoreboard.domain.model.factory;

import com.scoreboard.domain.handler.Handler;
import com.scoreboard.domain.handler.tiebreak.TiebreakHandler;
import com.scoreboard.domain.handler.game.AdvantageGameHandler;
import com.scoreboard.domain.handler.game.NoAdvantageGameHandler;
import com.scoreboard.domain.handler.match.MatchHandler;
import com.scoreboard.domain.handler.set.SetHandler;
import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.domain.model.TennisPlayer;
import com.scoreboard.domain.rules.TennisRules;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OngoingMatchFactory {
    private final TennisRules tennisRules;

    public OngoingMatch create(String firstPlayerName, String secondPlayerName) {
        TennisPlayer firstPlayer = new TennisPlayer(firstPlayerName);
        TennisPlayer secondPlayer = new TennisPlayer(secondPlayerName);

        Handler setHandler = new SetHandler(tennisRules.setRules());
        Handler matchHandler = new MatchHandler(tennisRules.matchRules());
        Handler tieBreakHandler = new TiebreakHandler(tennisRules.tiebreakRules());

        Handler gameHandler = tennisRules.gameRules().isAdvantageEnabled()
                ? new AdvantageGameHandler()
                : new NoAdvantageGameHandler();

        gameHandler.setNextHandler(setHandler);
        tieBreakHandler.setNextHandler(setHandler);
        setHandler.setNextHandler(matchHandler);

        return new OngoingMatch(firstPlayer, secondPlayer, gameHandler, tieBreakHandler);
    }
}
