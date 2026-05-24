defaultvalue: ${data.getDefaultValue()}
<#if !data.isEmptyScopes()>
scopes:
<#list data.scopes as scope>
<#if scope.hasNotNull()>
  ${scope.getName()}:
  <#if scope.getInit()??>
    init: |
  <#list scope.getInitLines() as line>
        '${line}'
  </#list>
  </#if>
  <#if scope.getGet()??>
    get: |
  <#list scope.getGetLines() as line>
        '${line}'
  </#list>
  </#if>
  <#if scope.getSet()??>
    set: |
  <#list scope.getSetLines() as line>
        '${line}'
  </#list>
  </#if>
  <#if scope.getRead()??>
    read: |
  <#list scope.getReadLines() as line>
        '${line}'
  </#list>
  </#if>
  <#if scope.getWrite()??>
    write: |
  <#list scope.getWriteLines() as line>
         '${line}'
  </#list>
  </#if>
</#if>
</#list>
</#if>