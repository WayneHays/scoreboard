package com.scoreboard.mapper;

import com.scoreboard.dto.FinishedMatchesPage;
import com.scoreboard.model.Match;

import java.util.List;

public class MatchesPageMapper {

    public FinishedMatchesPage map(int pageNumber, List<Match> matches,
                                   int totalPages, String filterByPlayerName) {
        return FinishedMatchesPage.builder()
                .pageNumber(pageNumber)
                .matches(matches)
                .totalPages(totalPages)
                .filterByPlayerName(filterByPlayerName)
                .build();
    }
}
