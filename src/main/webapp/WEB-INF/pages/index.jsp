<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
  <body>
    <c:forEach var="poll" items="${pollList}">
    <h2><a href="glasanje?pollID=${poll.id}">${poll.title}</a></h2>
    </c:forEach>
  </body>
</html>