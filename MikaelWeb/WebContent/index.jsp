<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Mikaels</title>
</head>
<body>
Test:
<form action="${pageContext.request.contextPath}/TestBaguette" method="POST">
	<input type="submit" value="Hello there" />
Volum: <a href="${pageContext.request.contextPath}/TestBaguette?getVolum">her</a><br />
Neste kilde: <input type="submit" value="Kilde" name="kilde"/><br />
</form>
</body>
</html>