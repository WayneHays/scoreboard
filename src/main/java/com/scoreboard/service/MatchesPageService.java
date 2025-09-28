package com.scoreboard.service;

import com.scoreboard.dto.FinishedMatchesPage;
import com.scoreboard.mapper.FinishedMatchesPageMapper;
import com.scoreboard.model.entity.Match;
import com.scoreboard.model.entity.Player;
import com.scoreboard.util.PaginationHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchesPageService {
    private final FindMatchesService findMatchesService;
    private final PlayerService playerService;
    private final FinishedMatchesPageMapper mapper;

    public MatchesPageService(FindMatchesService findMatchesService,
                              PlayerService playerService,
                              FinishedMatchesPageMapper mapper) {
        this.findMatchesService = findMatchesService;
        this.playerService = playerService;
        this.mapper = mapper;
    }

    public FinishedMatchesPage getAllMatchesPage(int pageNumber) {
        int totalPages = findMatchesService.getTotalCountOfPages();
        PaginationHelper.validatePageNumber(pageNumber, totalPages);

        List<Match> matches = findMatchesService.findMatchesByPage(pageNumber);
        return mapper.map(pageNumber, matches, totalPages, null);
    }

    public FinishedMatchesPage getPlayerMatchesPage(String playerName, int pageNumber) {
        List<Player> matchingPlayers = playerService.findByNameContaining(playerName);

        if (matchingPlayers.isEmpty()) {
            return mapper.map(pageNumber, Collections.emptyList(), 0, playerName);
        }

        if (matchingPlayers.size() == 1) {
            return getSinglePlayerMatches(matchingPlayers.get(0), pageNumber, playerName);
        }

        return getMultiplePlayersMatches(matchingPlayers, pageNumber, playerName);
    }

    private FinishedMatchesPage getSinglePlayerMatches(Player player, int pageNumber, String searchTerm) {
        int totalPages = findMatchesService.getTotalCountOfPagesByPlayer(player);
        PaginationHelper.validatePageNumber(pageNumber, totalPages);

        List<Match> matches = findMatchesService.findMatchesByPlayerByPage(player, pageNumber);
        return mapper.map(pageNumber, matches, totalPages, searchTerm);
    }

    private FinishedMatchesPage getMultiplePlayersMatches(List<Player> players, int pageNumber, String searchTerm) {
        List<Match> allMatches = collectAllMatches(players);
        int totalPages = PaginationHelper.calculateTotalPages(allMatches.size());

        PaginationHelper.validatePageNumber(pageNumber, totalPages);

        List<Match> paginatedMatches = PaginationHelper.paginateInMemory(allMatches, pageNumber);
        return mapper.map(pageNumber, paginatedMatches, totalPages, searchTerm);
    }

    private List<Match> collectAllMatches(List<Player> players) {
        List<Match> allMatches = new ArrayList<>();
        for (Player player : players) {
            allMatches.addAll(findMatchesService.findAllMatchesByPlayer(player));
        }
        return allMatches;
    }
}
