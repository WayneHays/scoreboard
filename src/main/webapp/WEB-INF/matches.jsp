<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<c:set var="baseUrl" value="${pageContext.request.contextPath}/matches" />

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Matches</title>
    <link href="${pageContext.request.contextPath}/css/matches.css?v=3" rel="stylesheet">
</head>
<body>
<div class="container">
<header>
    <h1 class="title">Match Results</h1>
</header>

<main>
<nav class="nav-buttons">
    <a href="${pageContext.request.contextPath}/home" class="nav-button">Home</a>
    <a href="${pageContext.request.contextPath}/new-match"
       class="nav-button results-button">New Match</a>
</nav>
    <section class="search-form">
        <t:error-list errors="${errors}"/>
        <form method="GET" action="${baseUrl}" class="search-form-grid">
            <input type="text"
                   name="playerName"
                   class="search-input"
                   placeholder="Filter by player name..."
                   value="<c:out value="${playerName}"/>">
            <input type="hidden" name="page" value="1">

            <c:choose>
                <c:when test="${not empty playerName and not empty errors}}">
                    <a href="${baseUrl}" class="search-button clear-mode">Clear</a>
                </c:when>
                <c:otherwise>
                    <button type="submit" class="search-button">Search</button>
                </c:otherwise>
            </c:choose>
        </form>
    </section>

    <c:if test="${not empty playerName and empty errors}">
        <div class="search-info">
            Results for: <strong><c:out value="${playerName}"/></strong>
        </div>
    </c:if>

    <section class="results-section">
        <c:choose>
            <c:when test="${empty page.matches}">
                <div class="no-results">
                    <c:choose>
                        <c:when test="${not empty playerName and empty errors}">
                            No matches found for player "<c:out value="${playerName}"/>"
                        </c:when>
                        <c:otherwise>
                            No matches found
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:when>
            <c:otherwise>
                <table class="matches-table">
                    <thead>
                    <tr>
                        <th>Player 1</th>
                        <th>Player 2</th>
                        <th>Winner</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${page.matches}" var="match">
                        <tr>
                            <td data-label="Player 1">
                                <c:out value="${match.firstPlayerName}"/>
                            </td>
                            <td data-label="Player 2">
                                <c:out value="${match.secondPlayerName}"/>
                            </td>
                            <td data-label="Winner">
                                    <span class="winner-name">
                                        <c:out value="${match.winnerName}"/>
                                    </span>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </section>
    <t:pagination totalPages="${page.totalPages}" pageNumber="${page.pageNumber}" baseUrl="${baseUrl}"/>
</main>
</div>
</body>
</html>