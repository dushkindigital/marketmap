<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>

<h3>Connected to Etsy</h3>

<form id="disconnect" method="post">
	<div class="formInfo">
		<p>The Libereco application is already connected to your Etsy account.
			Click the button if you wish to disconnect.			
	</div>

	<button type="submit">Disconnect</button>	
	<input type="hidden" name="_method" value="delete" />
</form>

