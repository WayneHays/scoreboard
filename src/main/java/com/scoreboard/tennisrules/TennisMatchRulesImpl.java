package com.scoreboard.tennisrules;

import com.scoreboard.tennisrules.standard.StandardGameRules;
import com.scoreboard.tennisrules.standard.StandardMatchRules;
import com.scoreboard.tennisrules.standard.StandardSetRules;
import com.scoreboard.tennisrules.standard.StandardTiebreakRules;
import com.scoreboard.validation.TennisRulesValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

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
        this.gameRules = Objects.requireNonNull(gameRules);
        this.tiebreakRules = Objects.requireNonNull(tiebreakRules);
        this.setRules = Objects.requireNonNull(setRules);
        this.matchRules = Objects.requireNonNull(matchRules);
        TennisRulesValidator.validate(this);
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
