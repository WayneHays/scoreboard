<%--
  Created by IntelliJ IDEA.
  User: Veretennikov
  Date: 09.08.2025
  Time: 14:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tennis Scoreboard</title>
    <link href="${pageContext.request.contextPath}/css/home.css" rel="stylesheet">
</head>
<body>
<h1>Welcome to Tennis Scoreboard!</h1>
<h2>Manage your tennis matches, record results and track rankings!</h2>

<img src=${pageContext.request.contextPath}/images/tennis_player.jpg
     alt="tennis player">

<div class="buttons-container">
    <a href="${pageContext.request.contextPath}/new-match" class="new-match-button">New match</a>
    <a href="${pageContext.request.contextPath}/matches" class="results-button">Results</a>
</div>

<footer>
    <p>© Tennis Scoreboard, project from <a href="https://zhukovsd.github.io/java-backend-learning-course/projects/tennis-scoreboard/">Java Backend Roadmap</a></p>
</footer>
</body>
</html>
