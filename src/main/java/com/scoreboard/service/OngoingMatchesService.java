package com.scoreboard.service;

import com.scoreboard.model.MatchWithScore;
import com.scoreboard.model.Player;
import com.scoreboard.model.Score;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OngoingMatchesService {
    private final Map<UUID, MatchWithScore> ongoingMatches;

    public OngoingMatchesService() {
        this.ongoingMatches = new ConcurrentHashMap<>();
    }

    public void create(Player first, Player second, Score score) {
        try {
            MatchWithScore matchWithScore = new MatchWithScore(first, second, score);
            UUID uuid = UUID.randomUUID();
            ongoingMatches.put(uuid, matchWithScore);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create match", e);
        }
    }

    public MatchWithScore find(UUID uuid) {
        return ongoingMatches.get(uuid);
    }
}
