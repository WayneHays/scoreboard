<%--
  Created by IntelliJ IDEA.
  User: Veretennikov
  Date: 05.08.2025
  Time: 15:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>New-match</title>
</head>
<body>
    <form method="POST" action="/new-match">
        <label>Player 1:</label>
        <input type="text" name="player1name" required>

        <label>Player 2:</label>
        <input type="text" name="player2name" required>

        <button type="submit">Start</button>
    </form>
</body>
</html>
