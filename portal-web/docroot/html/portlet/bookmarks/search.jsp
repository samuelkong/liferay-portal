<%--
/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

<%@ include file="/html/portlet/bookmarks/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

long breadcrumbsFolderId = ParamUtil.getLong(request, "breadcrumbsFolderId");

long searchFolderId = ParamUtil.getLong(request, "searchFolderId");
long searchFolderIds = ParamUtil.getLong(request, "searchFolderIds");

long[] folderIdsArray = null;

if (searchFolderId > 0) {
	folderIdsArray = new long[] {searchFolderId};
}
else {
	List folderIds = new ArrayList();

	folderIds.add(new Long(searchFolderIds));

	BookmarksFolderServiceUtil.getSubfolderIds(folderIds, scopeGroupId, searchFolderIds);

	folderIdsArray = StringUtil.split(StringUtil.merge(folderIds), 0L);
}

String keywords = ParamUtil.getString(request, "keywords");
%>

<liferay-portlet:renderURL varImpl="searchURL">
	<portlet:param name="struts_action" value="/bookmarks/search" />
</liferay-portlet:renderURL>

<aui:form action="<%= searchURL %>" method="get" name="fm">
	<liferay-portlet:renderURLParams varImpl="searchURL" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="breadcrumbsFolderId" type="hidden" value="<%= breadcrumbsFolderId %>" />
	<aui:input name="searchFolderId" type="hidden" value="<%= searchFolderId %>" />
	<aui:input name="searchFolderIds" type="hidden" value="<%= searchFolderIds %>" />

	<liferay-ui:header
		backURL="<%= redirect %>"
		title="search"
	/>

	<%
	PortletURL portletURL = renderResponse.createRenderURL();

	portletURL.setParameter("struts_action", "/bookmarks/search");
	portletURL.setParameter("redirect", redirect);
	portletURL.setParameter("breadcrumbsFolderId", String.valueOf(breadcrumbsFolderId));
	portletURL.setParameter("searchFolderId", String.valueOf(searchFolderId));
	portletURL.setParameter("searchFolderIds", String.valueOf(searchFolderIds));
	portletURL.setParameter("keywords", keywords);
	%>

	<liferay-ui:search-container
		emptyResultsMessage='<%= LanguageUtil.format(pageContext, "no-entries-were-found-that-matched-the-keywords-x", "<strong>" + HtmlUtil.escape(keywords) + "</strong>") %>'
		iteratorURL="<%= portletURL %>"
	>

		<%
		Indexer indexer = IndexerRegistryUtil.getIndexer(BookmarksEntry.class);

		SearchContext searchContext = SearchContextFactory.getInstance(request);

		searchContext.setAttribute("paginationType", "more");
		searchContext.setEnd(searchContainer.getEnd());
		searchContext.setFolderIds(folderIdsArray);
		searchContext.setKeywords(keywords);
		searchContext.setStart(searchContainer.getStart());

		Hits hits = indexer.search(searchContext);
		%>

		<liferay-ui:search-container-results
			results="<%= BookmarksUtil.getEntries(hits) %>"
			total="<%= hits.getLength() %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.portlet.bookmarks.model.BookmarksEntry"
			modelVar="entry"
		>

			<%
			entry = entry.toEscapedModel();

			BookmarksFolder folder = entry.getFolder();

			String rowHREF = themeDisplay.getPathMain().concat("/bookmarks/open_entry?entryId=").concat(String.valueOf(entry.getEntryId()));
			%>

			<liferay-ui:search-container-column-text
				name="#"
				value="<%= (index + 1) + StringPool.PERIOD %>"
			/>

			<liferay-ui:search-container-column-text
				href="<%= rowHREF %>"
				name="folder"
				target="_blank"
				title="<%= entry.getDescription() %>"
				value="<%= folder.getName() %>"
			/>

			<liferay-ui:search-container-column-text
				href="<%= rowHREF %>"
				name="entry"
				target="_blank"
				title="<%= entry.getDescription() %>"
				value="<%= entry.getName() %>"
			/>

			<liferay-ui:search-container-column-jsp
				path="/html/portlet/bookmarks/entry_action.jsp"
			/>
		</liferay-ui:search-container-row>

		<span class="aui-search-bar">
			<aui:input inlineField="<%= true %>" label="" name="keywords" size="30" title="search-bookmarks" type="text" value="<%= keywords %>" />

			<aui:button type="submit" value="search" />
		</span>

		<br /><br />

		<liferay-ui:search-iterator type="more" />
	</liferay-ui:search-container>
</aui:form>

<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) %>">
	<aui:script>
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />keywords);
	</aui:script>
</c:if>

<%
if (searchFolderId > 0) {
	BookmarksUtil.addPortletBreadcrumbEntries(searchFolderId, request, renderResponse);
}

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, "search") + ": " + keywords, currentURL);
%>