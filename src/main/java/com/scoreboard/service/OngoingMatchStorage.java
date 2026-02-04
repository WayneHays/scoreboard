package com.scoreboard.service;

import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.exception.MatchNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class OngoingMatchStorage {
    private final Map<UUID, OngoingMatch> ongoingMatches = new ConcurrentHashMap<>();

    public void add(UUID id, OngoingMatch match) {
        ongoingMatches.put(id, match);
    }

    public Optional<OngoingMatch> get(UUID id) {
        return Optional.ofNullable(ongoingMatches.get(id));
    }

    public void remove(UUID id) {
        ongoingMatches.remove(id);
    }

    public OngoingMatch compute(UUID id, Consumer<OngoingMatch> action) {
        return ongoingMatches.compute(id, (key, match) -> {
            if (match == null) {
                throw new MatchNotFoundException("Match with id " + id + " not found");
            }
            action.accept(match);
            return match;
        });
    }
}
