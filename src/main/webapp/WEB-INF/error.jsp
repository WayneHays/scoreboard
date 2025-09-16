<%--
  Created by IntelliJ IDEA.
  User: Veretennikov
  Date: 15.09.2025
  Time: 10:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${errorTitle} - ${statusCode}</title>
  <link href="${pageContext.request.contextPath}/css/error/error.css" rel="stylesheet">
</head>
<body>
<div class="container error-${statusCode}">
  <div class="error-icon">${errorIcon}</div>
  <h1 class="error-code">${statusCode}</h1>
  <h2 class="error-title">${errorTitle}</h2>

  <div class="error-message">
    <c:choose>
      <c:when test="${not empty errorMessage}">
        ${errorMessage}
      </c:when>
      <c:otherwise>
        ${defaultMessage}
      </c:otherwise>
    </c:choose>
  </div>

  <!-- Показываем запрошенный URL если доступен -->
  <c:if test="${not empty requestedUrl}">
    <div class="requested-url">
      Requested: <span class="url-text">${requestedUrl}</span>
    </div>
  </c:if>

  <!-- Показываем детали исключения для 500 ошибок -->
  <c:if test="${statusCode == 500 && not empty exceptionMessage}">
    <div class="exception-message">
      <strong>Error details:</strong> ${exceptionMessage}
    </div>
  </c:if>

  <!-- Дополнительная информация для 400 ошибок -->
  <c:if test="${statusCode == 400 && not empty validationErrors}">
    <div class="validation-errors">
      <strong>Validation errors:</strong>
      <ul>
        <c:forEach items="${validationErrors}" var="error">
          <li>${error}</li>
        </c:forEach>
      </ul>
    </div>
  </c:if>

  <div class="actions">
    <a href="${pageContext.request.contextPath}/home" class="button button-primary">
      🏠 Home
    </a>

    <!-- Дополнительные кнопки для разных типов ошибок -->
    <c:if test="${statusCode == 400}">
      <button onclick="history.back()" class="button button-secondary">
        ⬅️ Go Back
      </button>
    </c:if>

    <c:if test="${statusCode == 404}">
      <a href="${pageContext.request.contextPath}/matches" class="button button-secondary">
        🎾 View Matches
      </a>
    </c:if>
  </div>
</div>
</body>
</html>
