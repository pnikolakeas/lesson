<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript">
	window.onload = function() {
		document.forms[0].submit();
	};
</script>

<form action="${endpoint}" method="post" accept-charset="utf-8">

	<c:forEach items="${parameters}" var="parameter">
		<input type="hidden" name="${parameter.key}" value="${parameter.value}" />
	</c:forEach>

	<noscript>
		<p><fmt:message key="login.setup.message" /></p>
		<input type="submit" value="<fmt:message key="login.setup.submit" />" />
	</noscript>
</form>
