package com.scoreboard.dto.request;

import com.scoreboard.model.entity.Match;

import java.util.List;

public record MatchesPageContext(long pageNumber, List<Match> matches,
                                 long totalPages, String filterByPlayerName) {}
