<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
  <body>
    <h1>Error</h1>
    <p><%= request.getAttribute("error") %></p>
    <p>Go <a href="/webapp-baza/index.html">home</a>.</p>
  </body>
</html>
