<%--
  Created by IntelliJ IDEA.
  User: Veretennikov
  Date: 11.09.2025
  Time: 18:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Server Error - 500</title>
    <link href="${pageContext.request.contextPath}/css/error/500.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <div class="error-icon">⚠️</div>
    <h1 class="error-code">500</h1>
    <h2 class="error-title">Internal Server Error</h2>

    <div class="error-message">
        Something went wrong on our server. We're working to fix this issue.
    </div>

    <% if (request.getAttribute("requestedUrl") != null) { %>
    <div class="requested-url">
        Requested: <span class="url-text">${requestedUrl}</span>
    </div>
    <% } %>

    <% if (request.getAttribute("exceptionMessage") != null) { %>
    <div class="exception-message">
        <strong>Error details:</strong> ${exceptionMessage}
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