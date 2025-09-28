package com.scoreboard.config;

import com.scoreboard.dao.MatchDao;
import com.scoreboard.dao.PlayerDao;
import com.scoreboard.mapper.FinishedMatchesPageMapper;
import com.scoreboard.mapper.MatchLiveViewMapper;
import com.scoreboard.mapper.MatchResultMapper;
import com.scoreboard.mapper.NewMatchFormMapper;
import com.scoreboard.service.*;
import com.scoreboard.validator.PlayerNameValidator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor (access = AccessLevel.PRIVATE)
public final class ApplicationContext {
    private static final Map<Class<?>, Object> SERVICES = new ConcurrentHashMap<>();

    static {
        initialize();
    }

    private static void initialize() {
        try {
            PlayerDao playerDao = new PlayerDao();
            MatchDao matchDao = new MatchDao();

            FinishedMatchesPageMapper pageMapper = new FinishedMatchesPageMapper();
            NewMatchFormMapper newMatchFormMapper = new NewMatchFormMapper();
            MatchLiveViewMapper matchLiveViewMapper = new MatchLiveViewMapper();
            MatchResultMapper matchResultMapper = new MatchResultMapper();
            PlayerNameValidator nameValidator = new PlayerNameValidator();

            PlayerService playerService = new PlayerService(playerDao);
            FinishedMatchService finishedMatchService = new FinishedMatchService(matchDao);
            FindMatchesService findMatchesService = new FindMatchesService(matchDao);
            OngoingMatchesService ongoingMatchesService = new OngoingMatchesService();
            MatchGameplayService matchGameplayService = new MatchGameplayService();
            MatchesPageService matchesPageService = new MatchesPageService(
                    findMatchesService, playerService, pageMapper);

            register(PlayerDao.class, playerDao);
            register(MatchDao.class, matchDao);
            register(PlayerService.class, playerService);
            register(FinishedMatchService.class, finishedMatchService);
            register(FindMatchesService.class, findMatchesService);
            register(OngoingMatchesService.class, ongoingMatchesService);
            register(MatchGameplayService.class, matchGameplayService);
            register(MatchesPageService.class, matchesPageService);
            register(NewMatchFormMapper.class, newMatchFormMapper);
            register(MatchLiveViewMapper.class, matchLiveViewMapper);
            register(MatchResultMapper.class, matchResultMapper);
            register(PlayerNameValidator.class, nameValidator);
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
