package com.scoreboard.service.scorecalculation.handler;

import com.scoreboard.model.entity.Player;
import com.scoreboard.model.ongoingmatch.OngoingMatch;

public interface Handler{
    void setNext(Handler handler);
    void handle(OngoingMatch ongoingMatch, Player scorer);
}
