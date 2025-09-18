package com.scoreboard.service;

import com.scoreboard.dto.OngoingMatch;
import com.scoreboard.exception.NotFoundException;
import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.model.Score;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OngoingMatchesService {
    private static final OngoingMatchesService INSTANCE = new OngoingMatchesService();
    private final Map<UUID, OngoingMatch> ongoingMatches;

    public OngoingMatchesService() {
        this.ongoingMatches = new ConcurrentHashMap<>();
    }

    public static OngoingMatchesService getInstance() {
        return INSTANCE;
    }

    public UUID createMatch(Player first, Player second) {
        Match match = new Match(first, second);
        Score score = new Score(first, second);
        UUID uuid = UUID.randomUUID();

        OngoingMatch ongoingMatch = OngoingMatch.createNew(match, score, uuid);
        ongoingMatches.put(uuid, ongoingMatch);
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
