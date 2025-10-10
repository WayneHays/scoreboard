package com.scoreboard.config;

import com.scoreboard.dao.MatchDao;
import com.scoreboard.dao.PlayerDao;
import com.scoreboard.mapper.MatchesPageMapper;
import com.scoreboard.mapper.MatchLiveViewMapper;
import com.scoreboard.mapper.MatchResultMapper;
import com.scoreboard.service.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationContext {
    private static final Map<Class<?>, Object> SERVICES = new ConcurrentHashMap<>();

    static {
        initialize();
    }

    private static void initialize() {
        try {
            PlayerDao playerDao = new PlayerDao();
            MatchDao matchDao = new MatchDao();

            MatchesPageMapper matchesPageMapper = new MatchesPageMapper();
            MatchLiveViewMapper matchLiveViewMapper = new MatchLiveViewMapper();
            MatchResultMapper matchResultMapper = new MatchResultMapper();

            PaginationService paginationService = new PaginationService(matchesPageMapper);
            FinishedMatchPersistenceService finishedMatchPersistenceService =
                    new FinishedMatchPersistenceService(matchDao, playerDao);
            MatchQueryService matchQueryService = new MatchQueryService(matchDao, paginationService);
            OngoingMatchesService ongoingMatchesService = new OngoingMatchesService();
            ScoreCalculationService scoreCalculationService = new ScoreCalculationService();

            register(PlayerDao.class, playerDao);
            register(MatchDao.class, matchDao);
            register(MatchesPageMapper.class, matchesPageMapper);
            register(MatchLiveViewMapper.class, matchLiveViewMapper);
            register(MatchResultMapper.class, matchResultMapper);
            register(PaginationService.class, paginationService);
            register(FinishedMatchPersistenceService.class, finishedMatchPersistenceService);
            register(MatchQueryService.class, matchQueryService);
            register(OngoingMatchesService.class, ongoingMatchesService);
            register(ScoreCalculationService.class, scoreCalculationService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize services", e);
        }
    }

    private static <T> void register(Class<T> clazz, T instance) {
        if (SERVICES.containsKey(clazz)) {
            throw new RuntimeException("Service already registered: " + clazz.getName());
        }
        SERVICES.put(clazz, instance);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> type) {
        T service = (T) SERVICES.get(type);

        if (service == null) {
            throw new RuntimeException("Service not found: " + type.getName());
        }
        return service;
    }
}
