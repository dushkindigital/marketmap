<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>

<h3>Connect to Etsy</h3>

<form action="<c:url value="/connect/etsy" />" method="POST">
	<div class="formInfo">
		<p>
			You haven't created any connections with Etsy yet. Click the button to connect Etsy with your Etsy account. 
			(You'll be redirected to Etsy where you'll be asked to authorize the connection.)
		</p>
	</div>
	<p><button type="submit"><img src="<c:url value="/resources/images/etsy-logo.jpg" />"/></button></p>
</form>