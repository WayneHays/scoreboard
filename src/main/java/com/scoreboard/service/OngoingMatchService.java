package com.scoreboard.service;

import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.domain.model.factory.OngoingMatchFactory;
import com.scoreboard.domain.model.TennisPlayer;
import com.scoreboard.exception.MatchNotFoundException;
import com.scoreboard.exception.PlayerNotInMatchException;
import com.scoreboard.persistence.memory.OngoingMatchStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class OngoingMatchService {
    private static final String MATCH_CREATED_MSG = "Match created: UUID={}, players='{}' vs '{}'";
    private static final String MATCH_NOT_FOUND_MSG = "Match with id %s not found";
    private static final String PLAYER_NOT_IN_MATCH_MSG = "Player with name %s is not in match with id %s";

    private final OngoingMatchStorage storage;
    private final OngoingMatchFactory factory;

    public UUID createMatch(String firstPlayerName, String secondPlayerName) {
        OngoingMatch match = factory.create(firstPlayerName, secondPlayerName);
        UUID id = UUID.randomUUID();
        storage.put(id, match);

        log.info(MATCH_CREATED_MSG, id, firstPlayerName, secondPlayerName);
        return id;
    }

    public OngoingMatch getMatch(UUID id) {
        Optional<OngoingMatch> match = storage.get(id);

        if (match.isEmpty()) {
            String message = MATCH_NOT_FOUND_MSG.formatted(id);
            log.info(message);
            throw new MatchNotFoundException(message);
        }
        return match.get();
    }

    public void removeMatch(UUID id) {
        storage.remove(id);
    }

    public OngoingMatch computeMatch(UUID id, Consumer<OngoingMatch> action) {
        return storage.compute(id, action)
                .orElseThrow(() -> new MatchNotFoundException("Match with id " + id + " not found"));
    }

    public TennisPlayer ensurePlayerInMatch(UUID id, String name) {
        OngoingMatch match = getMatch(id);

        return match.ensurePlayer(name)
                .orElseThrow(() -> new PlayerNotInMatchException(PLAYER_NOT_IN_MATCH_MSG));
    }
}
