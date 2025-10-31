package com.scoreboard.service.ongoingmatches;

import com.scoreboard.exception.NotFoundException;
import com.scoreboard.model.entity.Player;
import com.scoreboard.model.domain.OngoingMatch;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OngoingMatchesService {
    private final Map<UUID, OngoingMatch> ongoingMatches;

    public OngoingMatchesService() {
        this.ongoingMatches = new ConcurrentHashMap<>();
    }

    public UUID createMatch(Player player1, Player player2) {
        OngoingMatch ongoing = new OngoingMatch(player1, player2);

        UUID uuid = UUID.randomUUID();
        ongoingMatches.put(uuid, ongoing);
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

    public boolean contains(UUID uuid) {
        return ongoingMatches.containsKey(uuid);
    }
}
