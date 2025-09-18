<%--
  Created by IntelliJ IDEA.
  User: Veretennikov
  Date: 06.08.2025
  Time: 10:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Match Result</title>
    <link href="${pageContext.request.contextPath}/css/match-result.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <h1 class="title">Match Finished!</h1>

    <div class="winner-announcement">
        <span class="winner-name">${matchResult.winnerName}</span>
        wins!
    </div>

    <!-- Final Score Display -->
    <div class="final-scoreboard">
        <div class="score-title">Final Score</div>

        <div class="final-score-row ${matchResult.firstPlayerRowClass}">
            <div class="player-info">
                <div class="player-name">${matchResult.firstPlayerName}</div>
            </div>
            <div class="sets-score">${matchResult.firstPlayerFinalSets}</div>
        </div>

        <div class="final-score-row ${matchResult.secondPlayerRowClass}">
            <div class="player-info">
                <div class="player-name">${matchResult.secondPlayerName}</div>
            </div>
            <div class="sets-score">${matchResult.secondPlayerFinalSets}</div>
        </div>
    </div>

    <!-- Navigation Links -->
    <div class="navigation-links">
        <a href="${pageContext.request.contextPath}/home" class="nav-link">
            Home
        </a>

        <a href="${pageContext.request.contextPath}/matches" class="nav-link matches-link">
            Matches
        </a>
    </div>
</div>
</body>
</html>