package com.scoreboard.service.matchprocess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MatchLockManager {
    private static final Logger logger = LoggerFactory.getLogger(MatchLockManager.class);

    private final ConcurrentHashMap<UUID, Object> locks = new ConcurrentHashMap<>();

    public void executeWithLock(UUID matchId, Runnable action) {
        Object lock = locks.computeIfAbsent(matchId, k -> new Object());
        synchronized (lock) {
            action.run();
        }
    }

    public void releaseLock(UUID matchId) {
        locks.remove(matchId);
        logger.debug("Lock released for match: {}", matchId);
    }
}
