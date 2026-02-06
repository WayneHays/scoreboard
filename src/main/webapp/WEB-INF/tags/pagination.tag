<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="totalPages" required="true" %>
<%@ attribute name="pageNumber" required="true" %>
<%@ attribute name="playerName" %>
<%@ attribute name="baseUrl" required="true" %>

<c:if test="${totalPages > 1}">
    <nav class="pagination">
        <c:choose>
            <c:when test="${pageNumber > 1}">
                <a href="${baseUrl}?page=${pageNumber - 1} <c:if test='${not empty playerName}'>&filter_by_player_name=<c:out value='${playerName}'/></c:if>"
                   class="pagination-item">Previous</a>
            </c:when>
            <c:otherwise>
                <span class="pagination-item disabled">Previous</span>
            </c:otherwise>
        </c:choose>

        <span class="pagination-item active">${pageNumber}</span>

        <c:choose>
            <c:when test="${pageNumber < totalPages}">
                <a href="${baseUrl}?page=${pageNumber + 1}<c:if test='${not empty playerName}'>&filter_by_player_name=<c:out value='${playerName}'/></c:if>"
                   class="pagination-item">Next</a>
            </c:when>
            <c:otherwise>
                <span class="pagination-item disabled">Next</span>
            </c:otherwise>
        </c:choose>
        <div class="page-info">
            Page ${pageNumber} of ${totalPages}
        </div>
    </nav>
</c:if>