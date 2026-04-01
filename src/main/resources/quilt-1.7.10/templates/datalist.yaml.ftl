<#list data.entries as entry>
<#if !entry.isBuiltIn()>
- ${entry.getName()}:
  readable_name: <#if entry.getReadableName()??>"${entry.getReadableName()}"<#else>"${entry.getName()}"</#if>
<#if entry.getType()??>  type: ${entry.getType()}</#if>
<#if entry.getTexture()??>  texture: ${entry.getTexture()}</#if><#if !entry.getOther().isEmpty()>
  other:<#list entry.getOthers() as oth>
    ${oth.getKey()}: ${oth.getValue()}</#list></#if></#if></#list>