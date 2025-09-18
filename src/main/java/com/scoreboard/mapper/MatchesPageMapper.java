package com.scoreboard.mapper;

import com.scoreboard.dto.MatchesPage;
import com.scoreboard.model.Match;

import java.util.List;

public class MatchesPageMapper {

    public MatchesPage map(int pageNumber, List<Match> matches,
                           int totalPages, String filterByPlayerName) {
        return MatchesPage.builder()
                .pageNumber(pageNumber)
                .matches(matches)
                .totalPages(totalPages)
                .filterByPlayerName(filterByPlayerName)
                .build();
    }
}
