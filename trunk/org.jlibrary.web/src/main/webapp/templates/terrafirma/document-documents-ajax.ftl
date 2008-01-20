<ul>
<#list node_collection as relation>
<#if relation.document>
<li> 
<input type="checkbox" id="doc${relation.id}" name="relations" value="${relation.id}" <#if document.relations.contains(relation)>checked="checked"</#if>/><label for="doc${relation.id}">${relation.name}</label>
</li>
</#if>
<#if relation.directory>
<li id="content${relation.id}"> 
	<a id="${relation.id}" onclick="loadDocuments(this);return false;" href="${root_url}/forward?method=documentdocuments&amp;repository=${repository.name}&amp;id=${document.id}&amp;parentId=${relation.id}">
		${relation.name}
	</a>
</li>
</#if>
</#list>
</ul>