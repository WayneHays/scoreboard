package com.scoreboard.context;

import com.scoreboard.dto.FinishedMatchDto;
import com.scoreboard.service.FinishedMatchPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TestDataInitializer {
    private final FinishedMatchPersistenceService persistenceService;

    public void loadTestData() {
        log.info("Loading test data...");

        saveMatch("Novak Djokovic", "Rafael Nadal", "Novak Djokovic");
        saveMatch("Novak Djokovic", "Roger Federer", "Roger Federer");
        saveMatch("Novak Djokovic", "Andy Murray", "Novak Djokovic");
        saveMatch("Novak Djokovic", "Dominic Thiem", "Novak Djokovic");
        saveMatch("Novak Djokovic", "Alexander Zverev", "Alexander Zverev");

        saveMatch("Rafael Nadal", "Roger Federer", "Rafael Nadal");
        saveMatch("Rafael Nadal", "Andy Murray", "Rafael Nadal");
        saveMatch("Rafael Nadal", "Dominic Thiem", "Dominic Thiem");
        saveMatch("Rafael Nadal", "Stefanos Tsitsipas", "Rafael Nadal");
        saveMatch("Rafael Nadal", "Daniil Medvedev", "Daniil Medvedev");

        saveMatch("Roger Federer", "Andy Murray", "Roger Federer");
        saveMatch("Roger Federer", "Stan Wawrinka", "Stan Wawrinka");
        saveMatch("Roger Federer", "Marin Cilic", "Roger Federer");
        saveMatch("Roger Federer", "Juan Martin del Potro", "Juan Martin del Potro");
        saveMatch("Roger Federer", "Grigor Dimitrov", "Roger Federer");

        saveMatch("Andy Murray", "Stan Wawrinka", "Andy Murray");
        saveMatch("Dominic Thiem", "Alexander Zverev", "Dominic Thiem");
        saveMatch("Stefanos Tsitsipas", "Daniil Medvedev", "Stefanos Tsitsipas");
        saveMatch("Carlos Alcaraz", "Jannik Sinner", "Carlos Alcaraz");
        saveMatch("Holger Rune", "Felix Auger-Aliassime", "Holger Rune");

        log.info("Test data loaded successfully: 20 matches created");
    }

    private void saveMatch(String firstPlayer, String secondPlayer, String winner) {
        FinishedMatchDto dto = new FinishedMatchDto(firstPlayer, secondPlayer, winner);
        persistenceService.saveFinishedMatch(dto);
    }
}
