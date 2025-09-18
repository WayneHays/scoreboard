package com.scoreboard.dto;

import com.scoreboard.model.Match;
import lombok.Builder;

import java.util.List;

@Builder
public record FinishedMatchesPage(int pageNumber,
                                  List<Match> matches,
                                  int totalPages,
                                  String filterByPlayerName) {
}
