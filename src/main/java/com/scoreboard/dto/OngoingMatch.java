package com.scoreboard.dto;

import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.model.Score;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class OngoingMatch {
    private final Match match;
    private final UUID uuid;
    private final Score score;

    @Setter
    private boolean isTieBreak;

    @Setter
    private Player advantage;

    public OngoingMatch(Match match, Score score, UUID uuid) {
        this.match = match;
        this.uuid = uuid;
        this.score = score;
        this.isTieBreak = false;
        this.advantage = null;
    }

    public static OngoingMatch createNew(Match match, Score score, UUID uuid) {
        return new OngoingMatch(match, score, uuid);
    }
}
