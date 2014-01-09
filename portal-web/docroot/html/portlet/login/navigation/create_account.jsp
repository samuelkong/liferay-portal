<%--
/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/html/portlet/login/init.jsp" %>

<%
String strutsAction = ParamUtil.getString(request, "struts_action");

String signInByPortlet = ParamUtil.getString(request, "signInByPortlet");

boolean showCreateAccountIcon = false;

if (!strutsAction.equals("/login/create_account") && company.isStrangers() && !portletName.equals(PortletKeys.FAST_LOGIN)) {
	showCreateAccountIcon = true;
}
%>

<c:if test="<%= showCreateAccountIcon %>">

	<%
	String createAccountURL = PortalUtil.getCreateAccountURL(request, themeDisplay);

	if (Validator.isNotNull(signInByPortlet)) {
		createAccountURL = HttpUtil.addParameter(createAccountURL, portletDisplay.getNamespace() + "signInByPortlet", signInByPortlet);
	}
	%>

	<liferay-ui:icon
		image="add_user"
		message="create-account"
		url="<%= createAccountURL %>"
	/>
</c:if>