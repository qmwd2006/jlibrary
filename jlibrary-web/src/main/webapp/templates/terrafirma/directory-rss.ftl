<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0">
  <channel>
    <title>${directory.name}</title>
    <description>${directory.description}</description>
		<link>${repository_url}${directory.path?replace("%", "%25")}</link>

		<#list directory.nodes as children>
  	  <#if children.document>
  		  <item>
  		    <title>${children.name}</title>
  		    <link>${repository_url}${children.path?replace("%", "%25")}</link>
  		    <guid isPermaLink="false">${children.id}</guid>
  		    <pubDate>${children.date?string("yyyy-MM-dd HH:mm:ss")}</pubDate>
  		    <description>${children.description}</description>
  		  </item>
  		</#if>
    </#list>
  </channel>
</rss>
