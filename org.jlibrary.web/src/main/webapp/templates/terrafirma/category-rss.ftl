<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0">
  <channel>
    <title>${category.name}</title>
    <description>${category.description}</description>
		<link>${categories_root_url}/${category.name}</link>

		<#list category_documents as children>
  	  <#if !children.directory>
  		  <item>
  		    <title>${children.name}</title>
  		    <link>${repository_url}${children.path}</link>
  		    <guid isPermaLink="false">${children.id}</guid>
  		    <pubDate>${children.date?string("yyyy-MM-dd HH:mm:ss")}</pubDate>
  		    <description>${children.description}</description>
  		  </item>
  		</#if>
    </#list>
  </channel>
</rss>
