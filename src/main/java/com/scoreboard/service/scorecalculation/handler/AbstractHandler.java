package com.scoreboard.service.scorecalculation.handler;

import com.scoreboard.model.entity.Player;
import com.scoreboard.model.domain.OngoingMatch;
import lombok.Setter;

@Setter
public abstract class AbstractHandler implements Handler {
    protected Handler next;

    @Override
    public final void handle(OngoingMatch match, Player scorer) {
        if (match.isFinished()) {
            return;
        }
        doHandle(match, scorer);
    }

    protected abstract void doHandle(OngoingMatch match, Player scorer);

    protected void callNext(OngoingMatch match, Player scorer) {
        if (next != null) {
            next.handle(match, scorer);
        }
    }
}
