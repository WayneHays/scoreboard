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
    <link href="${pageContext.request.contextPath}/css/new-match.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <header>
        <div class="game-icon">🎾</div>
        <h1 class="title">New Match</h1>
    </header>

    <main>
        <c:if test="${not empty commonErrors}">
          <ul>
              <c:forEach var="error" items="${commonErrors}">
                  <li>${error}</li>
              </c:forEach>
          </ul>
        </c:if>

        <form action="${pageContext.request.contextPath}/new-match" method="POST">
            <div>
                <c:if test="${not empty firstNameErrors}">
                    <ul>
                        <c:forEach var="error" items="${firstNameErrors}">
                            <li>${error}</li>
                        </c:forEach>
                    </ul>
                </c:if>
            </div>
            <div class="form-group">
                <label for="firstPlayer" class="form-label">Player 1</label>
                <input
                        type="text"
                        id="firstPlayer"
                        name="firstPlayerName"
                        class="form-input"
                        placeholder="First player's name"
                        value="<c:out value='${param.firstPlayerName}'/>"
                        required
                >
            </div>

            <div class="vs-divider">
                <div class="vs-line"></div>
                <div class="vs-text">VS</div>
                <div class="vs-line"></div>
            </div>
            <div>
                <c:if test="${not empty secondNameErrors}">
                    <ul>
                        <c:forEach var="error" items="${secondNameErrors}">
                            <li>${error}</li>
                        </c:forEach>
                    </ul>
                </c:if>
            </div>
            <div class="form-group">
                <label for="secondPlayer" class="form-label">Player 2</label>
                <input
                        type="text"
                        id="secondPlayer"
                        name="secondPlayerName"
                        class="form-input"
                        placeholder="Second player's name"
                        value="<c:out value='${param.secondPlayerName}'/>"
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
