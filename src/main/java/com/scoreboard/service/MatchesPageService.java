package com.scoreboard.service;

import com.scoreboard.dto.FinishedMatchesPage;
import com.scoreboard.exception.NotFoundException;
import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.mapper.MatchesPageMapper;

import java.util.List;
import java.util.Optional;

public class MatchesPageService {
    public static final int MINIMAL_VALID_PAGE = 1;
    private static final MatchesPageService INSTANCE = new MatchesPageService();

    private final FindMatchesService findMatchesService;
    private final PlayerService playerService;
    private final MatchesPageMapper mapper;

    private MatchesPageService() {
        this.findMatchesService = FindMatchesService.getInstance();
        this.playerService = PlayerService.getInstance();
        this.mapper = new MatchesPageMapper();
    }

    public static MatchesPageService getInstance() {
        return INSTANCE;
    }

    public FinishedMatchesPage getAllMatchesPage(int pageNumber) {
        int totalPages = findMatchesService.getTotalCountOfPages();

        if (isInvalidPage(pageNumber, totalPages)) {
            String message = (totalPages == 0) ? "No matches found" :
                    String.format("Page %d not found. Available pages: 1-%d", pageNumber, totalPages);
            throw new NotFoundException(message);
        }

        List<Match> matches = findMatchesService.findMatchesByPage(pageNumber);
        return mapper.map(pageNumber, matches, totalPages, null);
    }

    public FinishedMatchesPage getPlayerMatchesPage(String playerName, int pageNumber) {
        Optional<Player> maybePlayer = playerService.find(playerName);

        if (maybePlayer.isEmpty()) {
            throw new NotFoundException("Player not found: " + playerName);
        }

        Player player = maybePlayer.get();
        int totalPages = findMatchesService.getTotalCountOfPagesByPlayer(player);

        if (isInvalidPage(pageNumber, totalPages)) {
            throw new NotFoundException(String.format("Page %d not found. Available pages: 1-%d",
                    pageNumber, totalPages));
        }

        List<Match> matches = findMatchesService.findMatchesByPlayerByPage(player, pageNumber);
        return mapper.map(pageNumber, matches, totalPages, playerName);
    }

    private boolean isInvalidPage(int pageNumber, int totalPages) {
        return pageNumber < MINIMAL_VALID_PAGE || (totalPages > 0 && pageNumber > totalPages);
    }
}
