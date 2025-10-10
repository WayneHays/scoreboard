<%--
  Created by IntelliJ IDEA.
  User: Veretennikov
  Date: 05.08.2025
  Time: 13:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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

            <div class="score-row">
                <div class="player-name"><c:out value="${matchView.firstPlayerName}"/></div>
                <div class="sets-score">${matchView.firstPlayerSets}</div>
                <div class="games-score">${matchView.firstPlayerGames}</div>
                <div class="points-score">${matchView.firstPlayerPoints}</div>
            </div>

            <div class="score-row">
                <div class="player-name"><c:out value="${matchView.secondPlayerName}"/></div>
                <div class="sets-score">${matchView.secondPlayerSets}</div>
                <div class="games-score">${matchView.secondPlayerGames}</div>
                <div class="points-score">${matchView.secondPlayerPoints}</div>
            </div>
        </section>

        <section class="actions-container">
            <form action="${pageContext.request.contextPath}/match-score" method="POST" class="point-form">
                <input type="hidden" name="uuid" value="${param.uuid}">
                <input type="hidden" name="playerName" value="${matchView.firstPlayerName}">
                <button type="submit" class="action-button">
                    <c:out value="${matchView.firstPlayerName}"/> Wins Point
                </button>
            </form>

            <form action="${pageContext.request.contextPath}/match-score" method="POST" class="point-form">
                <input type="hidden" name="uuid" value="${param.uuid}">
                <input type="hidden" name="playerName" value="${matchView.secondPlayerName}">
                <button type="submit" class="action-button player2-button">
                    <c:out value="${matchView.secondPlayerName}"/> Wins Point
                </button>
            </form>
        </section>
    </main>
</div>
</body>
</html>
