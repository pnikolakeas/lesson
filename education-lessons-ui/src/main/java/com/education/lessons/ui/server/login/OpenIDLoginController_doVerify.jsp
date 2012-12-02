<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript">
	window.onload = function() {

		<c:choose>
			<c:when test="${not empty sessionScope.contextUser}">
				window.opener.session.signIn();
			</c:when>
			<c:otherwise>
				var obj = {};

				<c:forEach items="${attributes}" var="attribute">
					obj["${attribute.key}"] = "${attribute.value}";
				</c:forEach>

				window.opener.session.signUp(obj);
			</c:otherwise>
		</c:choose>

		window.close();
	};
</script>
