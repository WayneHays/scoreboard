package com.scoreboard.mapper;

import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.dto.MatchResponse.OngoingMatchDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OngoingMatchMapper {

    @Mapping(source = "firstPlayer.name", target = "firstPlayerName")
    @Mapping(source = "secondPlayer.name", target = "secondPlayerName")
    @Mapping(expression = "java(match.getPoints(match.getFirstPlayer()).getValue())", target = "firstPlayerPoints")
    @Mapping(expression = "java(match.getPoints(match.getSecondPlayer()).getValue())", target = "secondPlayerPoints")
    @Mapping(expression = "java(match.getGames(match.getFirstPlayer()))", target = "firstPlayerGames")
    @Mapping(expression = "java(match.getGames(match.getSecondPlayer()))", target = "secondPlayerGames")
    @Mapping(expression = "java(match.getTieBreakPoints(match.getFirstPlayer()))", target = "firstPlayerTieBreakPoints")
    @Mapping(expression = "java(match.getTieBreakPoints(match.getSecondPlayer()))", target = "secondPlayerTieBreakPoints")
    @Mapping(expression = "java(match.getSets(match.getFirstPlayer()))", target = "firstPlayerSets")
    @Mapping(expression = "java(match.getSets(match.getSecondPlayer()))", target = "secondPlayerSets")
    @Mapping(expression = "java(match.isTieBreak())", target = "isTieBreak")
    @Mapping(expression = "java(match.isFinished())", target = "isFinished")
    OngoingMatchDto toDto(OngoingMatch match);
}
