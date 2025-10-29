package com.scoreboard.service.scorecalculation;

import com.scoreboard.model.entity.Player;
import com.scoreboard.model.ongoingmatch.OngoingMatch;
import com.scoreboard.service.scorecalculation.handler.Handler;
import com.scoreboard.service.scorecalculation.handler.MatchHandler;
import com.scoreboard.service.scorecalculation.handler.SetHandler;
import com.scoreboard.service.scorecalculation.handler.TiebreakHandler;
import com.scoreboard.service.scorecalculation.handler.game_handler.GameHandler;
import com.scoreboard.service.scorecalculation.handler.game_handler.GameHandlerFactory;
import com.scoreboard.service.scorecalculation.rules.*;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ScoreCalculationService {
    private final TennisMatchRules rules;
    private final Handler handlerChain;

    public ScoreCalculationService() {
        this(TennisMatchRules.standard());
    }

    public ScoreCalculationService(TennisMatchRules rules) {
        this.rules = rules;
        this.handlerChain = createHandlerChain();
    }

    public void awardPoint(OngoingMatch match, Player scorer) {
        handlerChain.handle(match, scorer);
    }

    private Handler createHandlerChain() {
        GameHandler gameHandler = GameHandlerFactory.create(rules.gameRules());
        TiebreakHandler tiebreakHandler = new TiebreakHandler(rules.tiebreakRules());
        SetHandler setHandler = new SetHandler(rules.setRules());
        MatchHandler matchHandler = new MatchHandler(rules.matchRules());

        gameHandler.setNext(tiebreakHandler);
        tiebreakHandler.setNext(setHandler);
        setHandler.setNext(matchHandler);

        return gameHandler;
    }
}
