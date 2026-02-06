package com.scoreboard.context;

import com.scoreboard.dto.MatchResponse.FinishedMatchDto;
import com.scoreboard.service.FinishedMatchPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TestDataInitializer {
    private static final String WAWRINKA = "Stan Wawrinka";
    private static final String CILIC = "Marin Cilic";
    private static final String DJOKOVIC = "Novak Djokovic";
    private static final String MURRAY = "Andy Murray";
    private static final String THIEM = "Dominic Thiem";
    private static final String ZVEREV = "Alexander Zverev";
    private static final String TSITSIPAS = "Stefanos Tsitsipas";
    private static final String MEDVEDEV = "Daniil Medvedev";
    private static final String DEL_POTRO = "Juan Martin del Potro";
    private static final String DIMITROV = "Grigor Dimitrov";
    private static final String ALCARAZ = "Carlos Alcaraz";
    private static final String SINNER = "Jannik Sinner";
    private static final String RUNE = "Holger Rune";
    private static final String ALIASSIME = "Felix Auger-Aliassime";
    private static final String NADAL = "Rafael Nadal";
    private static final String FEDERER = "Roger Federer";

    private final FinishedMatchPersistenceService persistenceService;

    public void loadTestData() {
        log.info("Loading test data...");

        saveMatch(DJOKOVIC, NADAL, DJOKOVIC);
        saveMatch(DJOKOVIC, FEDERER, FEDERER);
        saveMatch(DJOKOVIC, MURRAY, DJOKOVIC);
        saveMatch(DJOKOVIC, THIEM, DJOKOVIC);
        saveMatch(DJOKOVIC, ZVEREV, ZVEREV);

        saveMatch(NADAL, FEDERER, NADAL);
        saveMatch(NADAL, MURRAY, NADAL);
        saveMatch(NADAL, THIEM, THIEM);
        saveMatch(NADAL, TSITSIPAS, NADAL);
        saveMatch(NADAL, MEDVEDEV, MEDVEDEV);

        saveMatch(FEDERER, MURRAY, FEDERER);
        saveMatch(FEDERER, WAWRINKA, WAWRINKA);
        saveMatch(FEDERER, CILIC, FEDERER);
        saveMatch(FEDERER, DEL_POTRO, DEL_POTRO);
        saveMatch(FEDERER, DIMITROV, FEDERER);

        saveMatch(MURRAY, WAWRINKA, MURRAY);
        saveMatch(THIEM, ZVEREV, ZVEREV);
        saveMatch(TSITSIPAS, MEDVEDEV, TSITSIPAS);
        saveMatch(ALCARAZ, SINNER, ALCARAZ);
        saveMatch(RUNE, ALIASSIME, RUNE);

        log.info("Test data loaded successfully: 20 matches created");
    }

    private void saveMatch(String firstPlayer, String secondPlayer, String winner) {
        FinishedMatchDto dto = new FinishedMatchDto(firstPlayer, secondPlayer, winner);
        persistenceService.saveFinishedMatch(dto);
    }
}
