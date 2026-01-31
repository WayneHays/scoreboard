package com.scoreboard.service.scorecalculation.handler;

import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.model.domain.PlayerDto;
import com.scoreboard.model.entity.Player;
import com.scoreboard.service.scorecalculation.PointResult;
import lombok.Setter;

@Setter
public abstract class AbstractHandler implements Handler {
    protected Handler next;

    @Override
    public final PointResult handle(OngoingMatch ongoingMatch, PlayerDto scorer) {
        doHandle(scorer);
    }

    protected abstract void doHandle(Player scorer);

    protected void callNext(Player scorer) {
        if (next != null) {
            next.handle(, scorer);
        }
    }
}
