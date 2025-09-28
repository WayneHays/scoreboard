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

<c:set var="error" value="${errorPageData}" />

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><c:out value="${error.errorTitle}"/> - ${error.statusCode}</title>
  <link href="${pageContext.request.contextPath}/css/error/error.css" rel="stylesheet">
</head>
<body>
<div class="container error-${error.statusCode}">
  <div class="error-icon">${error.errorIcon}</div>
  <h1 class="error-code">${error.statusCode}</h1>
  <h2 class="error-title"><c:out value="${error.errorTitle}"/></h2>

  <div class="error-message">
    <c:choose>
      <c:when test="${not empty error.errorMessage}">
        <c:out value="${error.errorMessage}" escapeXml="true"/>
      </c:when>
      <c:otherwise>
        <c:out value="${error.defaultMessage}" escapeXml="true"/>
      </c:otherwise>
    </c:choose>
  </div>

  <c:if test="${not empty error.requestedUrl}">
    <div class="requested-url">
      Requested: <span class="url-text"><c:out value="${error.requestedUrl}" escapeXml="true"/></span>
    </div>
  </c:if>

  <div class="actions">
    <a href="${pageContext.request.contextPath}/home" class="button button-primary">
      🏠 Home
    </a>

    <c:if test="${error.statusCode == 400}">
      <button onclick="history.back()" class="button button-secondary">
        ⬅️ Go Back
      </button>
    </c:if>

    <c:if test="${error.statusCode == 404}">
      <a href="${pageContext.request.contextPath}/matches" class="button button-secondary">
        🎾 View Matches
      </a>
    </c:if>
  </div>
</div>
</body>
</html>
