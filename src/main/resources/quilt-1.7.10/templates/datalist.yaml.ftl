<#list data.entries as entry>
<#if !entry.isBuiltIn()>
- ${entry.getName()}<#if entry.hasAttributes()>:
<#if entry.getReadableName()??>  readable_name: "${entry.getReadableName()}"</#if>
<#if entry.getType()??>  type: ${entry.getType()}</#if>
<#if entry.getTexture()??>  texture: ${entry.getTexture()}</#if><#if !entry.getOther().isEmpty()>
  other:<#list entry.getOthers() as oth>
    ${oth.getKey()}: ${oth.getValue()}</#list></#if></#if></#list>
</#if>