<%--
  Created by IntelliJ IDEA.
  User: Veretennikov
  Date: 06.08.2025
  Time: 10:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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
    <header>
        <h1 class="title">Match Finished!</h1>
    </header>

    <main>
        <div class="winner-announcement">
            <span class="winner-name"><c:out value="${matchResult.winnerName}"/></span>
            wins!
        </div>

        <section class="final-scoreboard">
            <div class="score-title">Final Score</div>

            <div class="final-score-row ${matchResult.firstPlayerRowClass}">
                <div class="player-info">
                    <div class="player-name"><c:out value="${matchResult.firstPlayerName}"/></div>
                </div>
                <div class="sets-score">${matchResult.firstPlayerFinalSets}</div>
            </div>

            <div class="final-score-row ${matchResult.secondPlayerRowClass}">
                <div class="player-info">
                    <div class="player-name"><c:out value="${matchResult.secondPlayerName}"/></div>
                </div>
                <div class="sets-score">${matchResult.secondPlayerFinalSets}</div>
            </div>
        </section>
    </main>

    <nav class="navigation-links">
        <a href="${pageContext.request.contextPath}/home" class="nav-link">
            Home
        </a>

        <a href="${pageContext.request.contextPath}/matches" class="nav-link matches-link">
            Matches
        </a>
    </nav>
</div>
</body>
</html>