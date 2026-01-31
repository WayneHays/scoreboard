package com.scoreboard.service.scorecalculation.handler;

import com.scoreboard.model.entity.Player;
import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.service.scorecalculation.PointResult;
import com.scoreboard.model.domain.Points;

public class WithAdvantageGameHandler extends GameHandler {

    @Override
    protected PointResult handleGamePoint(OngoingMatch ongoingMatch, Player scorer, Player opponent) {
        Points scorerPoints = ongoingMatch.getPoints(scorer);
        Points opponentPoints = ongoingMatch.getPoints(opponent);

        if (scorerPoints == Points.ADVANTAGE) {
            ongoingMatch.awardGameTo(scorer);
            return PointResult.GAME_FINISHED;
        }

        if (opponentPoints == Points.ADVANTAGE) {
            ongoingMatch.resetToDeuce(opponent);
            ongoingMatch.setAdvantage(null);
            return PointResult.ADVANTAGE_RESET;
        }

        if (scorerPoints == Points.FORTY && opponentPoints != Points.FORTY) {
            ongoingMatch.awardGameTo(scorer);
            return PointResult.GAME_FINISHED;
        }

        if (scorerPoints == Points.FORTY && opponentPoints == Points.FORTY) {
            ongoingMatch.awardPointTo(scorer);
            ongoingMatch.setAdvantage(scorer);
            return PointResult.POINT_AWARDED;
        }

        ongoingMatch.awardPointTo(scorer);
        return PointResult.POINT_AWARDED;
    }
}
