package com.scoreboard.service.scorecalculation;

import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.model.entity.Player;
import com.scoreboard.service.scorecalculation.handler.*;
import com.scoreboard.tennisrules.TennisMatchRules;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
public class ScoreCalculationService {
    private static final Logger logger = LoggerFactory.getLogger(ScoreCalculationService.class);

    private final TennisMatchRules rules;
    private final Handler handlerChain;

    public ScoreCalculationService(TennisMatchRules rules) {
        if (rules == null) {
            throw new IllegalArgumentException("TennisMatchRules cannot be null");
        }

        this.rules = rules;
        this.handlerChain = createHandlerChain();

        logger.info("ScoreCalculationService initialized with rules: {}",
                rules.getClass().getSimpleName());
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
