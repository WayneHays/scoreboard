<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%@ attribute name="playerName" required="true" %>
<%@ attribute name="sets" required="true" %>
<%@ attribute name="games" required="true" %>
<%@ attribute name="points" required="true" %>
<%@ attribute name="tieBreakPoints" required="true" %>
<%@ attribute name="isTieBreak" required="true" type="java.lang.Boolean" %>

<div class="score-row">
    <div class="player-name"><c:out value="${playerName}"/></div>
    <div class="sets-score">${sets}</div>
    <div class="games-score">${games}</div>
    <div class="points-score">
        <c:choose>
            <c:when test="${isTieBreak}">
                ${tieBreakPoints}
            </c:when>
            <c:otherwise>
                ${points}
            </c:otherwise>
        </c:choose>
    </div>
</div>