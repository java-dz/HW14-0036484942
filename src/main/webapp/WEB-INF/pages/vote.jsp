<%@page import="hr.fer.zemris.java.hw14.VotingUtil.Poll"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% Poll poll = (Poll) request.getAttribute("poll"); %>
<html>
  <body>
    <h1><%= poll.title %></h1>
    <p><%= poll.message %></p>
    <ol>
      <c:forEach var="info" items="${infoList}">
      <li><a href="glasanje-glasaj?pollID=<%= poll.id %>&id=${info.id}">${info.name}</a></li>
      </c:forEach>
    </ol>
    <p>Idi <a href="/webapp-baza/index.html">kući</a>.</p>
  </body>
</html>