package com.scoreboard.service;

import com.scoreboard.model.Match;
import com.scoreboard.model.MatchWithScore;
import com.scoreboard.model.Player;
import com.scoreboard.model.Score;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OngoingMatchesService {
    private static final OngoingMatchesService INSTANCE = new OngoingMatchesService();
    private final Map<UUID, MatchWithScore> ongoingMatches;

    public OngoingMatchesService() {
        this.ongoingMatches = new ConcurrentHashMap<>();
    }

    public static OngoingMatchesService getInstance() {
        return INSTANCE;
    }

    public UUID create(Player first, Player second, Score score) {
        try {
            Match match = new Match(first, second);
            MatchWithScore matchWithScore = new MatchWithScore(match, score);
            UUID uuid = UUID.randomUUID();
            ongoingMatches.put(uuid, matchWithScore);
            return uuid;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create match", e);
        }
    }

    public MatchWithScore find(UUID uuid) {
        return ongoingMatches.get(uuid);
    }

    public void delete(UUID uuid) {
        ongoingMatches.remove(uuid);
    }
}
