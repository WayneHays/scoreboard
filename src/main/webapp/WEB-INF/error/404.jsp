<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Page Not Found - 404</title>
    <link href="${pageContext.request.contextPath}/css/error/404.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <div class="error-icon">❌</div>
    <h1 class="error-code">404</h1>
    <h2 class="error-title">Page Not Found</h2>

    <div class="error-message">
        <c:choose>
            <c:when test="${not empty errorMessage}">
                ${errorMessage}
            </c:when>
            <c:otherwise>
                The page you are looking for doesn't exist or has been moved.
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Показываем запрошенный URL если доступен -->
    <% if (request.getAttribute("requestedUrl") != null) { %>
    <div class="requested-url">
        Requested: <span class="url-text">${requestedUrl}</span>
    </div>
    <% } %>

    <div class="actions">
        <a href="${pageContext.request.contextPath}/home" class="button button-primary">
            🏠 Home
        </a>
    </div>
</div>
</body>
</html>