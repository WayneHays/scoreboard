package com.scoreboard.service.scorecalculation.handler.game_handler;

import com.scoreboard.service.scorecalculation.rules.GameRules;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameHandlerFactory {

    public static GameHandler create(GameRules gameRules) {
        return gameRules.isAdvantageEnabled() ?
                new WithAdvantageGameHandler() :
                new WithoutAdvantageGameHandler();
    }
}
