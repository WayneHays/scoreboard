<%--
  Created by IntelliJ IDEA.
  User: Veretennikov
  Date: 05.08.2025
  Time: 15:18
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
    <title>New Match</title>
    <link href="${pageContext.request.contextPath}/css/new-match.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <header>
        <div class="game-icon">🎾</div>
        <h1 class="title">New Match</h1>
    </header>

    <main>
        <t:error-list errors="${commonErrors}"/>
        <form action="${pageContext.request.contextPath}/new-match" method="POST">
            <t:player-input text="Player 1"
                            id="firstPlayer"
                            name="firstPlayerName"
                            placeholder="First player's name"
                            value="${param.firstPlayerName}"
                            errors="${firstNameErrors}"/>

            <div class="vs-divider">
                <div class="vs-line"></div>
                <div class="vs-text">VS</div>
                <div class="vs-line"></div>
            </div>

            <t:player-input text="Player 2"
                            id="secondPlayer"
                            name="secondPlayerName"
                            placeholder="Second player's name"
                            value="${param.secondPlayerName}"
                            errors="${secondNameErrors}"/>
            <button type="submit" class="start-button">
                Start Match!
            </button>
        </form>
    </main>
</div>
</body>
</html>
