<#if (maxBlogsEntryCount > 0)>
	<#list 1..maxBlogsEntryCount as blogsEntryCount>
		<#assign blogsEntry = dataFactory.addBlogsEntry(groupId, firstUserId, "Test Blog " + blogsEntryCount, "testblog" + blogsEntryCount, "This is a test blog " + blogsEntryCount + ".")>

		${sampleSQLBuilder.insertBlogsEntry(blogsEntry)}

		<#assign mbThreadId = counter.get()>
		<#assign mbRootMessageId = counter.get()>

		${sampleSQLBuilder.insertMBDiscussion(groupId, firstUserId, dataFactory.blogsEntryClassNameId, blogsEntry.entryId, mbThreadId, mbRootMessageId, maxBlogsEntryCommentCount)}

		${writerBlogsCSV.write(blogsEntry.entryId + "," + blogsEntry.urlTitle + "," + mbThreadId + "," + mbRootMessageId + "\n")}
	</#list>
</#if>