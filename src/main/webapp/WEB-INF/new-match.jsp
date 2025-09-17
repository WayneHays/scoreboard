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
    <title>New match</title>
    <link href="${pageContext.request.contextPath}/css/new_match.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <div class="game-icon">🎮</div>
    <h1 class="title">New match</h1>

    <c:if test="${newMatchForm.hasGeneralError()}">
        <div class="error-message general-error">
                ${newMatchForm.generalError}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/new-match" method="POST">
        <div class="form-group">
            <label for="player1" class="form-label">Player 1</label>
            <input
                    type="text"
                    id="player1"
                    name="player1name"
                    class="form-input ${newMatchForm.hasPlayer1Error() ? 'error' : ''}"
                    placeholder="first player's name"
                    value="${newMatchForm.player1Value}"
                    required
            >
            <c:if test="${newMatchForm.hasPlayer1Error()}">
                <div class="error-message field-error">
                        ${newMatchForm.player1Error}
                </div>
            </c:if>
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
                    class="form-input ${newMatchForm.hasPlayer2Error() ? 'error' : ''}"
                    placeholder="second player's name"
                    value="${newMatchForm.player2Value}"
                    required
            >
            <c:if test="${newMatchForm.hasPlayer2Error()}">
                <div class="error-message field-error">
                        ${newMatchForm.player2Error}
                </div>
            </c:if>
        </div>

        <button type="submit" class="start-button">
            Start!
        </button>
    </form>
</div>
</body>
</html>
