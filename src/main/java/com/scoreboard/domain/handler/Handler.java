package com.scoreboard.domain.handler;

import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.domain.model.TennisPlayer;
import lombok.Setter;

@Setter
public abstract class Handler {
    protected Handler nextHandler;

    public final PointResult handle(OngoingMatch match, TennisPlayer scorer) {
        PointResult result = doHandle(match, scorer);

        if (shouldPassToNext(result) && nextHandler != null) {
            return nextHandler.handle(match, scorer);
        }

        return result;
    }

    protected abstract PointResult doHandle(OngoingMatch match, TennisPlayer scorer);

    protected abstract boolean shouldPassToNext(PointResult result);
}
