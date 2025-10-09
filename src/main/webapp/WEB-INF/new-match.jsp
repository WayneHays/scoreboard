<%--
  Created by IntelliJ IDEA.
  User: Veretennikov
  Date: 05.08.2025
  Time: 15:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>New Match</title>
    <link href="${pageContext.request.contextPath}/css/new_match.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <header>
        <div class="game-icon">🎾</div>
        <h1 class="title">New Match</h1>
    </header>

    <main>
        <c:if test="${not empty error}">
            <div class="error-message general-error">
                <c:out value="${error}"/>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/new-match" method="POST">
            <div class="form-group">
                <label for="player1" class="form-label">Player 1</label>
                <input
                        type="text"
                        id="player1"
                        name="player1name"
                        class="form-input"
                        placeholder="First player's name"
                        value="<c:out value='${player1Input}'/>"
                        required
                >
            </div>

            <div class="vs-divider">
                <div class="vs-line"></div>
                <div class="vs-text">VS</div>
                <div class="vs-line"></div>
            </div>

            <div class="form-group">
                <label for="player2" class="form-label">Player 2</label>
                <input
                        type="text"
                        id="player2"
                        name="player2name"
                        class="form-input"
                        placeholder="Second player's name"
                        value="<c:out value='${player2Input}'/>"
                        required
                >
            </div>

            <button type="submit" class="start-button">
                Start Match!
            </button>
        </form>
    </main>
</div>
</body>
</html>
