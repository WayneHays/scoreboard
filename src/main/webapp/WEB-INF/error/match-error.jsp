<%--
  Created by IntelliJ IDEA.
  User: Veretennikov
  Date: 11.09.2025
  Time: 18:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Match Error</title>
    <link href="${pageContext.request.contextPath}/css/error/match-error.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <div class="error-icon">🎾</div>
    <h1 class="error-code">Match Error</h1>
    <h2 class="error-title">${errorTitle}</h2>

    <div class="error-message">
        ${errorDescription}
    </div>

    <div class="actions">
        <a href="${pageContext.request.contextPath}/new-match" class="button button-primary">
            Start New Match
        </a>
        <a href="${pageContext.request.contextPath}/matches" class="button button-secondary">
            View Results
        </a>
    </div>
</div>
</body>
</html>