package com.scoreboard.startup;

import com.scoreboard.persistence.dao.MatchDao;
import com.scoreboard.persistence.dao.MatchDaoImpl;
import com.scoreboard.persistence.dao.PlayerDao;
import com.scoreboard.persistence.dao.PlayerDaoImpl;
import com.scoreboard.mapper.FinishedMatchMapper;
import com.scoreboard.mapper.MatchResultMapper;
import com.scoreboard.mapper.OngoingMatchMapper;
import com.scoreboard.domain.model.factory.OngoingMatchFactory;
import com.scoreboard.service.BaseTransactionalService;
import com.scoreboard.service.FinishedMatchPersistenceService;
import com.scoreboard.service.MatchesPageService;
import com.scoreboard.service.OngoingMatchService;
import com.scoreboard.persistence.OngoingMatchStorage;
import com.scoreboard.service.MatchFacade;
import com.scoreboard.domain.rules.GameRules;
import com.scoreboard.domain.rules.MatchRules;
import com.scoreboard.domain.rules.SetRules;
import com.scoreboard.domain.rules.TennisRules;
import com.scoreboard.domain.rules.TiebreakRules;
import com.scoreboard.errorhandler.ErrorHandler;
import com.scoreboard.validation.PlayerNameValidator;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@WebListener
public class ApplicationContextListener implements ServletContextListener {
    private static final String SUCCESS_MESSAGE = "ApplicationContext initialized and stored in ServletContext";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Starting initialization ApplicationContext");

        ServletContext servletContext = sce.getServletContext();
        ApplicationContext applicationContext = new ApplicationContext();

        TennisRules tennisRules = createTennisRules();
        PlayerNameValidator playerNameValidator = new PlayerNameValidator();
        FinishedMatchMapper finishedMatchMapper = Mappers.getMapper(FinishedMatchMapper.class);
        MatchResultMapper matchResultMapper = Mappers.getMapper(MatchResultMapper.class);
        OngoingMatchMapper ongoingMatchMapper = Mappers.getMapper(OngoingMatchMapper.class);
        OngoingMatchFactory ongoingMatchFactory = new OngoingMatchFactory(tennisRules);
        ErrorHandler errorHandler = new ErrorHandler();
        PlayerDao playerDao = new PlayerDaoImpl();
        MatchDao matchDao = new MatchDaoImpl();
        BaseTransactionalService baseTransactionalService = new BaseTransactionalService();
        OngoingMatchStorage ongoingMatchStorage = new OngoingMatchStorage();
        OngoingMatchService ongoingMatchService = new OngoingMatchService(ongoingMatchStorage, ongoingMatchFactory);
        FinishedMatchPersistenceService finishedMatchPersistenceService = new FinishedMatchPersistenceService(matchDao, playerDao, baseTransactionalService);
        MatchesPageService matchesPageService = new MatchesPageService(matchDao, playerNameValidator, finishedMatchMapper, baseTransactionalService);
        MatchFacade matchFacade = new MatchFacade(ongoingMatchService, finishedMatchPersistenceService, ongoingMatchMapper, finishedMatchMapper, matchResultMapper);

        applicationContext.register(TennisRules.class, tennisRules);
        applicationContext.register(PlayerNameValidator.class, playerNameValidator);
        applicationContext.register(FinishedMatchMapper.class, finishedMatchMapper);
        applicationContext.register(MatchResultMapper.class, matchResultMapper);
        applicationContext.register(OngoingMatchMapper.class, ongoingMatchMapper);
        applicationContext.register(OngoingMatchFactory.class, ongoingMatchFactory);
        applicationContext.register(ErrorHandler.class, errorHandler);
        applicationContext.register(PlayerDao.class, playerDao);
        applicationContext.register(MatchDao.class, matchDao);
        applicationContext.register(BaseTransactionalService.class, baseTransactionalService);
        applicationContext.register(OngoingMatchStorage.class, ongoingMatchStorage);
        applicationContext.register(OngoingMatchService.class, ongoingMatchService);
        applicationContext.register(FinishedMatchPersistenceService.class, finishedMatchPersistenceService);
        applicationContext.register(MatchesPageService.class, matchesPageService);
        applicationContext.register(MatchFacade.class, matchFacade);

        validateRequiredServices(applicationContext);
        servletContext.setAttribute(ServletContext.class.getSimpleName(), applicationContext);
        log.info(SUCCESS_MESSAGE);

        TestDataInitializer testDataInitializer = new TestDataInitializer(finishedMatchPersistenceService);
        testDataInitializer.loadTestData();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        servletContext.removeAttribute(ServletContext.class.getSimpleName());
    }

    private void validateRequiredServices(ApplicationContext context) {
        List<Class<?>> requiredServices = List.of(
                TennisRules.class,
                PlayerNameValidator.class,
                FinishedMatchMapper.class,
                MatchResultMapper.class,
                OngoingMatchMapper.class,
                OngoingMatchFactory.class,
                ErrorHandler.class,
                PlayerDao.class,
                MatchDao.class,
                BaseTransactionalService.class,
                OngoingMatchStorage.class,
                OngoingMatchService.class,
                FinishedMatchPersistenceService.class,
                MatchesPageService.class,
                MatchFacade.class
        );

        List<String> missingServices = new ArrayList<>();

        for (Class<?> serviceClass : requiredServices) {
            try {
                context.get(serviceClass);
            } catch (IllegalStateException e) {
                missingServices.add(serviceClass.getSimpleName());
            }
        }

        if (!missingServices.isEmpty()) {
            String errorMessage = "Application startup failed! Missing required services: " +
                                  String.join(", ", missingServices);
            log.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        log.info("All required services validated successfully");
    }

    private TennisRules createTennisRules() {
        MatchRules matchRules = new MatchRules();
        SetRules setRules = new SetRules();
        GameRules gameRules = new GameRules();
        TiebreakRules tiebreakRules = new TiebreakRules();
        return new TennisRules(matchRules, setRules, gameRules, tiebreakRules);
    }
}
