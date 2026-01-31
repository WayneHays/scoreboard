package com.scoreboard.service.scorecalculation.handler;

import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.model.domain.TennisPlayer;
import com.scoreboard.service.scorecalculation.PointResult;

public interface Handler {
    void setNext(Handler handler);

    PointResult handle(OngoingMatch ongoingMatch, TennisPlayer scorer);
}
