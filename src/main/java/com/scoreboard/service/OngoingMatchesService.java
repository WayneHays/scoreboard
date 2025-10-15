package com.scoreboard.service;

import com.scoreboard.config.ServiceProvider;
import com.scoreboard.exception.NotFoundException;
import com.scoreboard.model.OngoingMatch;
import com.scoreboard.model.Score;
import com.scoreboard.model.entity.Match;
import com.scoreboard.model.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OngoingMatchesService implements ServiceProvider {
    private final Map<UUID, OngoingMatch> ongoingMatches;

    public OngoingMatchesService() {
        this.ongoingMatches = new ConcurrentHashMap<>();
    }

    public UUID createMatch(Player first, Player second) {
        Match match = new Match(first, second);
        Score score = new Score(first, second);
        UUID uuid = UUID.randomUUID();
        ongoingMatches.put(uuid, new OngoingMatch(match, score));
        return uuid;
    }

    public OngoingMatch get(UUID uuid) {
        OngoingMatch ongoingMatch = ongoingMatches.get(uuid);

        if (ongoingMatch == null) {
            throw new NotFoundException("Match not found");
        }

        return ongoingMatch;
    }

    public void delete(UUID uuid) {
        ongoingMatches.remove(uuid);
    }
}
