<#list data.mappingsContent as entry>
<#if entry.isEdited()>
<#if entry.getMappingContent().size() == 1>
${entry.getName()}: ${entry.getFirst()}
<#else>
${entry.getName()}:
<#list entry.getMappingContent() as content>
  - ${content}
</#list>
</#if>
</#if>
</#list>