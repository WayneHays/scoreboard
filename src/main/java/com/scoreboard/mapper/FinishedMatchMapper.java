package com.scoreboard.mapper;

import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.dto.FinishedMatchDto;
import com.scoreboard.entity.Match;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface FinishedMatchMapper {

    @Mapping(target = "firstPlayerName", expression = "java(match.getFirstPlayer().getName())")
    @Mapping(target = "secondPlayerName", expression = "java(match.getSecondPlayer().getName())")
    @Mapping(target = "winnerName", expression = "java(match.getWinner().getName())")
    FinishedMatchDto toDto(Match match);

    @Mapping(target = "firstPlayerName", expression = "java(ongoingMatch.getFirstPlayer().name())")
    @Mapping(target = "secondPlayerName", expression = "java(ongoingMatch.getSecondPlayer().name())")
    @Mapping(target = "winnerName", expression = "java(ongoingMatch.getWinnerName().orElseThrow(() -> new IllegalStateException(\"Match is finished, winner should be set\")))")
    FinishedMatchDto toDto(OngoingMatch ongoingMatch);
}
