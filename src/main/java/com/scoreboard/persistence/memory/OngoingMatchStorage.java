package com.scoreboard.persistence.memory;

import com.scoreboard.domain.model.OngoingMatch;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class OngoingMatchStorage {
    private final Map<UUID, OngoingMatch> ongoingMatches = new ConcurrentHashMap<>();

    public void put(UUID id, OngoingMatch match) {
        ongoingMatches.put(id, match);
    }

    public Optional<OngoingMatch> get(UUID id) {
        return Optional.ofNullable(ongoingMatches.get(id));
    }

    public void remove(UUID id) {
        ongoingMatches.remove(id);
    }

    public Optional<OngoingMatch> compute(UUID id, Consumer<OngoingMatch> action) {
        OngoingMatch result = ongoingMatches.compute(id, (key, match) -> {
            if (match == null) {
                return null;
            }
            action.accept(match);
            return match;
        });

        return Optional.ofNullable(result);
    }
}
