<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<link type="text/css" rel="stylesheet" href="css/LessonsUI.css">
		<link type="text/css" rel="stylesheet" href="css/gxt-gray.css">
		<link type="text/css" rel="stylesheet" href="css/gxt-all.css">
		<title>Web Application Education Project</title>
	</head>
	<body>
		<div id="loading">
			<div class="loading-indicator">
				<img src="images/gxt/shared/large-loading.gif"/> 
				<span id="loading-msg">Please wait ...</span>

				<script type="text/javascript" src="LessonsUI/LessonsUI.nocache.js"></script>

				<c:if test="${not empty contextUser}">
					<script type="text/javascript">
						window.contextUser = {
							isAdmin  : "${contextUser.admin}",
							nickname : "${contextUser.nickname}",
							fullname : "${contextUser.fullname}"
						};
						
						window.contextPath = "${pageContext.request.contextPath}";
					</script>
				</c:if>			
			</div>
		</div>

		<!-- OPTIONAL: include this if you want history support -->
		<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1'
			style="position: absolute; width: 0; height: 0; border: 0"></iframe>

		<!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
		<noscript>
			<div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red;border: 1px solid red; padding: 4px;">
				Your web browser must have JavaScript enabled in order for this application to display correctly.
			</div>
		</noscript>
	</body>
</html>
