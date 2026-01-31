package com.scoreboard.service;

import com.scoreboard.exception.MatchNotFoundException;
import com.scoreboard.exception.PairNameValidationException;
import com.scoreboard.exception.PlayerNotInMatchException;
import com.scoreboard.model.domain.OngoingMatchFactory;
import com.scoreboard.model.domain.TennisPlayer;
import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.validation.NameValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class OngoingMatchesService {
    private static final String PLAYER_NOT_IN_MATCH_MSG = "Player with name %s is not in match with id %s";
    private static final String MATCH_CREATED_MSG = "Match created: UUID={}, players='{}' vs '{}'";
    private static final String MATCH_NOT_FOUND_MSG = "Match with id %s not found";

    private final OngoingMatchFactory ongoingMatchFactory;
    private final NameValidator nameValidator;
    private final Map<UUID, OngoingMatch> ongoingMatches = new ConcurrentHashMap<>();

    public UUID createMatch(String firstPlayerName, String secondPlayerName) {
        throwIfHasAnyErrors(firstPlayerName, secondPlayerName);

        TennisPlayer firstPlayer = new TennisPlayer(firstPlayerName);
        TennisPlayer secondPlayer = new TennisPlayer(secondPlayerName);
        OngoingMatch ongoingMatch = ongoingMatchFactory.create(firstPlayer, secondPlayer);
        UUID id = UUID.randomUUID();
        ongoingMatches.put(id, ongoingMatch);
        log.info(MATCH_CREATED_MSG, id, firstPlayerName, secondPlayerName);
        return id;
    }

    private void throwIfHasAnyErrors(String firstPlayerName, String secondPlayerName) {
        List<String> firstNameErrors = nameValidator.validate(firstPlayerName);
        List<String> secondNameErrors = nameValidator.validate(secondPlayerName);
        List<String> commonErrors = nameValidator.validateDuplicate(firstPlayerName, secondPlayerName);

        if (hasAnyErrors(commonErrors, firstNameErrors, secondNameErrors)) {
            log.info("Name validation failed: first={}, second={}, common={}", firstNameErrors, secondNameErrors, commonErrors);
            throw new PairNameValidationException("Name validation failed: ", commonErrors, firstNameErrors, secondNameErrors);
        }
    }

    private boolean hasAnyErrors(List<String> commonErrors, List<String> firstNameErrors, List<String> secondNameErrors) {
        return ObjectUtils.isNotEmpty(commonErrors)
               || ObjectUtils.isNotEmpty(firstNameErrors)
               || ObjectUtils.isNotEmpty(secondNameErrors);
    }

    public Optional<OngoingMatch> getMatch(UUID id) {
        return Optional.ofNullable(ongoingMatches.get(id));
    }

    public void removeMatch(UUID id) {
        ongoingMatches.remove(id);
    }

    public OngoingMatch computeMatch(UUID id, Consumer<OngoingMatch> action) {
        return ongoingMatches.compute(id, (key, match) -> {
            if (match == null) {
                String message = String.format(MATCH_NOT_FOUND_MSG, id);
                log.error(message);
                throw new MatchNotFoundException(message);
            }
            action.accept(match);
            return match;
        });
    }

    public TennisPlayer ensurePlayerInMatch(UUID matchId, String playerName) {
        OngoingMatch ongoingMatch = getMatch(matchId)
                .orElseThrow(() -> new IllegalStateException(String.format(MATCH_NOT_FOUND_MSG, matchId)));

        if (ongoingMatch.getFirstPlayerName().equalsIgnoreCase(playerName)) {
            return ongoingMatch.getFirstPlayer();
        }

        if (ongoingMatch.getSecondPlayerName().equalsIgnoreCase(playerName)) {
            return ongoingMatch.getSecondPlayer();
        }

        String message = String.format(PLAYER_NOT_IN_MATCH_MSG, playerName, matchId);
        log.error(message);
        throw new PlayerNotInMatchException(message);
    }
}
