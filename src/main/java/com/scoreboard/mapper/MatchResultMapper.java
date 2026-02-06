package com.scoreboard.mapper;

import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.dto.MatchResultDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MatchResultMapper {

    @Mapping(expression = "java(match.getWinnerName().orElseThrow(() -> new IllegalStateException(\"Match is finished, winner must be set\")))", target = "winnerName")
    @Mapping(expression = "java(match.getFirstPlayer().name())", target = "firstPlayerName")
    @Mapping(expression = "java(match.getSecondPlayer().name())", target = "secondPlayerName")
    @Mapping(expression = "java(match.getSets(match.getFirstPlayer()))", target = "firstPlayerSets")
    @Mapping(expression = "java(match.getSets(match.getSecondPlayer()))", target = "secondPlayerSets")
    MatchResultDto toDto(OngoingMatch match);
}
