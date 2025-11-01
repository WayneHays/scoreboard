package com.scoreboard.service.scorecalculation.rules;

import com.scoreboard.service.scorecalculation.rules.standard.StandardGameRules;
import com.scoreboard.service.scorecalculation.rules.standard.StandardMatchRules;
import com.scoreboard.service.scorecalculation.rules.standard.StandardSetRules;
import com.scoreboard.service.scorecalculation.rules.standard.StandardTiebreakRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TennisMatchRulesImpl implements TennisMatchRules {
    private static final Logger logger = LoggerFactory.getLogger(TennisMatchRulesImpl.class);

    private final GameRules gameRules;
    private final TiebreakRules tiebreakRules;
    private final SetRules setRules;
    private final MatchRules matchRules;

    public TennisMatchRulesImpl() {
        logger.debug("Initializing standard tennis match rules");
        this.gameRules = new StandardGameRules();
        this.tiebreakRules = new StandardTiebreakRules();
        this.setRules = new StandardSetRules();
        this.matchRules = new StandardMatchRules();
    }

    public TennisMatchRulesImpl(GameRules gameRules, TiebreakRules tiebreakRules,
                                SetRules setRules, MatchRules matchRules) {
        logger.debug("Creating TennisMatchRulesImpl with custom rules");
        this.gameRules = gameRules;
        this.tiebreakRules = tiebreakRules;
        this.setRules = setRules;
        this.matchRules = matchRules;
    }

    @Override
    public GameRules gameRules() {
        return gameRules;
    }

    @Override
    public TiebreakRules tiebreakRules() {
        return tiebreakRules;
    }

    @Override
    public SetRules setRules() {
        return setRules;
    }

    @Override
    public MatchRules matchRules() {
        return matchRules;
    }
}
