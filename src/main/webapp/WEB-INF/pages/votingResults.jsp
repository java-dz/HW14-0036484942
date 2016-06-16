<%@page import="java.util.List"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
  <head>
    <style type="text/css">
      table.rez td {text-align: center;}
    </style>
  </head>
  <body>
    <h1>Rezultati glasanja</h1>
    <p>Ovo su rezultati glasanja.</p>
    
    <table border="1" class="rez" style="border-spacing: 0">
      <thead><tr><th>Naslov</th><th>Broj glasova</th></tr></thead>
      <tbody>
      
      <c:forEach var="info" items="${infoList}">
        <tr><td>${info.name}</td><td>${info.votes}</td></tr>
      </c:forEach>
      
      </tbody>
    </table>
    
    <h2>Grafički prikaz rezultata</h2>
    <img alt="Pie-chart" src="glasanje-grafika?pollID=<%= request.getAttribute("pollID") %>" />
    
    <h2>Rezultati u XLS formatu</h2>
    <p>Rezultati u XLS formatu dostupni su <a href="glasanje-xls?pollID=<%= request.getAttribute("pollID") %>">ovdje</a></p>
    
    <h2>Razno</h2>
    <p>Linkovi pobjednika:</p>
    <ul>
      <c:forEach var="winners" items="${winners}">
      <li><a href="${winners.link}" target="_blank">${winners.name}</a></li>
      </c:forEach>
    </ul>
    <p>Idi <a href="/webapp-baza/index.html">kući</a>.</p>
  </body>
</html>