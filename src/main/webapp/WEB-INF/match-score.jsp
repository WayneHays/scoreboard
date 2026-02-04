<%--
  Created by IntelliJ IDEA.
  User: Veretennikov
  Date: 05.08.2025
  Time: 13:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Match Score</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/match-score.css">
</head>
<body>
<div class="container">
    <header>
        <h1 class="title">Match Score</h1>
    </header>

    <main>
        <section class="scoreboard">
            <div class="score-header">
                <div>Player</div>
                <div>Sets</div>
                <div>Games</div>
                <div>Points</div>
            </div>

            <t:player-score playerName="${matchDto.firstPlayerName}"
                            sets="${matchDto.firstPlayerSets}"
                            games="${matchDto.firstPlayerGames}"
                            points="${matchDto.firstPlayerPoints}"
                            tieBreakPoints="${matchDto.firstPlayerTieBreakPoints}"
                            isTieBreak="${matchDto.isTieBreak}"/>
            <t:player-score playerName="${matchDto.secondPlayerName}"
                            sets="${matchDto.secondPlayerSets}"
                            games="${matchDto.secondPlayerGames}"
                            points="${matchDto.secondPlayerPoints}"
                            tieBreakPoints="${matchDto.secondPlayerTieBreakPoints}"
                            isTieBreak="${matchDto.isTieBreak}"/>
        </section>

        <section class="actions-container">
            <t:player-action-button playerName="${matchDto.firstPlayerName}"/>
            <t:player-action-button playerName="${matchDto.secondPlayerName}"/>
        </section>
    </main>
</div>
</body>
</html>
