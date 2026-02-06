package com.scoreboard.service;

import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.domain.model.factory.OngoingMatchFactory;
import com.scoreboard.domain.model.TennisPlayer;
import com.scoreboard.exception.MatchNotFoundException;
import com.scoreboard.exception.PlayerNotInMatchException;
import com.scoreboard.persistence.OngoingMatchStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class OngoingMatchService {
    private static final String MSG_MATCH_NOT_FOUND = "Match with id %s not found";
    private static final String MSG_PLAYER_NOT_IN_MATCH = "Player with name %s is not in match with id %s";
    private static final String LOG_CREATION_START = "Start match creation with players: {}, {}";
    private static final String LOG_CREATION_SUCCESS = "Match created: UUID={}, players='{}' vs '{}'";
    private static final String LOG_REMOVING_START = "Start removing match with id: {}";
    private static final String LOG_REMOVING_SUCCESS = "Removing succeed";
    private static final String LOG_COMPUTING_START = "Start computing match with id: {}";
    private static final String LOG_COMPUTING_SUCCESS = "Computing succeed";
    private static final String LOG_ENSURE_START = "Start ensure player {} in match with id {}";
    private static final String LOG_ENSURE_SUCCESS = "Ensure success";

    private final OngoingMatchStorage storage;
    private final OngoingMatchFactory factory;

    public UUID createMatch(String firstPlayerName, String secondPlayerName) {
        log.info(LOG_CREATION_START, firstPlayerName, secondPlayerName);

        OngoingMatch match = factory.create(firstPlayerName, secondPlayerName);
        UUID id = UUID.randomUUID();
        storage.put(id, match);

        log.info(LOG_CREATION_SUCCESS, id, firstPlayerName, secondPlayerName);
        return id;
    }

    public OngoingMatch getMatch(UUID id) {
        Optional<OngoingMatch> match = storage.get(id);

        if (match.isEmpty()) {
            String message = MSG_MATCH_NOT_FOUND.formatted(id);
            log.info(message);
            throw new MatchNotFoundException(message);
        }
        return match.get();
    }

    public void removeMatch(UUID id) {
        log.info(LOG_REMOVING_START, id.toString());
        storage.remove(id);
        log.info(LOG_REMOVING_SUCCESS);
    }

    public OngoingMatch computeMatch(UUID id, Consumer<OngoingMatch> action) {
        log.info(LOG_COMPUTING_START, id.toString());
        OngoingMatch match = storage.compute(id, action)
                .orElseThrow(() -> new MatchNotFoundException(MSG_MATCH_NOT_FOUND.formatted(id.toString())));
        log.info(LOG_COMPUTING_SUCCESS);
        return match;
    }

    public TennisPlayer ensurePlayerInMatch(UUID id, String name) {
        OngoingMatch match = getMatch(id);
        log.info(LOG_ENSURE_START, name, id.toString());
        TennisPlayer player = match.ensurePlayer(name)
                .orElseThrow(() -> new PlayerNotInMatchException(MSG_PLAYER_NOT_IN_MATCH));
        log.info(LOG_ENSURE_SUCCESS);
        return player;
    }
}
