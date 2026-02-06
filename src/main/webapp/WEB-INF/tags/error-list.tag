<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="errors" required="true" type="java.util.List" %>

<c:if test="${not empty errors}">
    <ul class="error-list">
        <c:forEach var="error" items="${errors}">
            <li class="error-item">${error}</li>
        </c:forEach>
    </ul>
</c:if>