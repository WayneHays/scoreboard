<%@tag body-content="empty" %>
<%@taglib prefix="c" uri="jakarta.tags.core" %>

<%@attribute name="playerName" required="true" %>

<form action="${pageContext.request.contextPath}/match-score" method="POST" class="point-form">
    <input type="hidden" name="uuid" value="${param.uuid}">
    <input type="hidden" name="playerName" value="${playerName}">
    <button type="submit" class="action-button">
        <c:out value="${playerName}"/> Wins Point
    </button>
</form>