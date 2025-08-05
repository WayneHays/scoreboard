package com.scoreboard.service;

import com.scoreboard.dao.MatchDao;
import com.scoreboard.dao.PlayersDao;
import com.scoreboard.model.Match;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OngoingMatchesService {
    private final PlayersDao playersDao;
    private final Map<UUID, Match> ongoingMatches;

    public OngoingMatchesService() {
        this.playersDao = new PlayersDao();
        this.ongoingMatches = new ConcurrentHashMap<>();
    }

    public void save(Match match) {
        try {
            UUID uuid = UUID.randomUUID();
            ongoingMatches.put(uuid, match);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save match", e);
        }
    }

    public Match find(UUID uuid) {
        return ongoingMatches.get(uuid);
    }
}
