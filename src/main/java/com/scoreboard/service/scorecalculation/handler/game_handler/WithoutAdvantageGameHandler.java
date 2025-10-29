package com.scoreboard.service.scorecalculation.handler.game_handler;

import com.scoreboard.model.entity.Player;
import com.scoreboard.model.ongoingmatch.OngoingMatch;
import com.scoreboard.service.scorecalculation.PointResult;
import com.scoreboard.service.scorecalculation.Points;

public class WithoutAdvantageGameHandler extends GameHandler {

    @Override
    protected PointResult handleGamePoint(OngoingMatch ongoingMatch, Player scorer, Player opponent) {
       Points scorerPoints = ongoingMatch.getPoints(scorer);
       Points opponentPoints = ongoingMatch.getPoints(opponent);

       if (scorerPoints == Points.FORTY && opponentPoints == Points.FORTY) {
           ongoingMatch.awardGameTo(scorer);
           return PointResult.GAME_FINISHED;
       }

       if (scorerPoints == Points.FORTY) {
           ongoingMatch.awardGameTo(scorer);
           return PointResult.GAME_FINISHED;
       }

        ongoingMatch.awardPointTo(scorer);
        return PointResult.POINT_AWARDED;
    }
}
