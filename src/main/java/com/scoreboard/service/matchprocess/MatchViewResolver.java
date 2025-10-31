package com.scoreboard.service.matchprocess;

import com.scoreboard.constant.JspPaths;
import com.scoreboard.mapper.MatchLiveViewMapper;
import com.scoreboard.mapper.MatchResultMapper;
import com.scoreboard.model.domain.OngoingMatch;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MatchViewResolver {
    private final MatchLiveViewMapper liveViewMapper;
    private final MatchResultMapper resultMapper;

    public Object resolveView(OngoingMatch match) {
        return match.isFinished() ? resultMapper.map(match) : liveViewMapper.map(match);
    }

    public String resolveJspPath(OngoingMatch match) {
        return match.isFinished() ? JspPaths.MATCH_RESULT_JSP : JspPaths.MATCH_SCORE_JSP;
    }

    public String resolveAttributeName(OngoingMatch match) {
        return match.isFinished() ? "matchResult" : "matchView";
    }
}
