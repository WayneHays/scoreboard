<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

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

<c:if test="${page.hasValidationError()}">
    <div class="error-message general-error">
        <c:out value="${page.validationError}"/>
    </div>
</c:if>
    <section class="search-form">
        <form method="GET" action="${baseUrl}" class="search-form-grid">
            <input type="text"
                   name="filter_by_player_name"
                   class="search-input"
                   placeholder="Search by player name..."
                   value="<c:out value="${page.hasValidationError() ? page.invalidPlayerName : page.filterByPlayerName}"/>">
            <input type="hidden" name="page" value="1">

            <c:choose>
                <c:when test="${page.hasFilter || page.hasValidationError()}">
                    <a href="${baseUrl}" class="search-button clear-mode">Clear</a>
                </c:when>
                <c:otherwise>
                    <button type="submit" class="search-button">Search</button>
                </c:otherwise>
            </c:choose>
        </form>
    </section>

    <c:if test="${page.hasFilter}">
        <div class="search-info">
            Results for: <strong><c:out value="${page.filterByPlayerName}"/></strong>
        </div>
    </c:if>

    <section class="results-section">
        <c:choose>
            <c:when test="${empty page.matches}">
                <div class="no-results">
                    <c:choose>
                        <c:when test="${page.hasFilter}">
                            No matches found for player "<c:out value="${page.filterByPlayerName}"/>"
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
                            <!-- ✅ ДОБАВЛЕНЫ data-label атрибуты -->
                            <td data-label="Player 1">
                                <c:out value="${match.firstPlayer.name}"/>
                            </td>
                            <td data-label="Player 2">
                                <c:out value="${match.secondPlayer.name}"/>
                            </td>
                            <td data-label="Winner">
                                    <span class="winner-name">
                                        <c:out value="${match.winner.name}"/>
                                    </span>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </section>

    <c:if test="${page.shouldShowPagination}">
        <nav class="pagination">
            <c:choose>
                <c:when test="${page.hasPrevious}">
                    <a href="${baseUrl}?page=${page.previousPage}<c:if test='${page.hasFilter}'>&filter_by_player_name=<c:out value='${page.filterByPlayerName}'/></c:if>"
                       class="pagination-item">Previous</a>
                </c:when>
                <c:otherwise>
                    <span class="pagination-item disabled">Previous</span>
                </c:otherwise>
            </c:choose>

            <span class="pagination-item active">${page.pageNumber}</span>

            <c:choose>
                <c:when test="${page.hasNext}">
                    <a href="${baseUrl}?page=${page.nextPage}<c:if test='${page.hasFilter}'>&filter_by_player_name=<c:out value='${page.filterByPlayerName}'/></c:if>"
                       class="pagination-item">Next</a>
                </c:when>
                <c:otherwise>
                    <span class="pagination-item disabled">Next</span>
                </c:otherwise>
            </c:choose>
        </nav>
    </c:if>

    <c:if test="${page.totalPages > 1}">
        <div class="page-info">
            Page ${page.pageNumber} of ${page.totalPages}
        </div>
    </c:if>
</main>
</div>
</body>
</html>