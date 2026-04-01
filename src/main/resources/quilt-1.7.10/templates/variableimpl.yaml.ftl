defaultvalue: ${data.getDefaultValue()}
<#if !data.scopes.isEmpty()>
scopes:
<#list data.scopes as scope>
<#if scope.hasNotNull()>
  ${scope.getName()}:
  <#if scope.getInit()??>
  <#if scope.getInitLines().size() == 1>
    init: ${scope.getInit()}
  <#else>
    init: |
  <#list scope.getInitLines() as line>
        ${line}
  </#list>
  </#if>
  </#if>
  <#if scope.getGet()??>
  <#if scope.getGetLines().size() == 1>
    get: ${scope.getGet()}
  <#else>
    get: |
  <#list scope.getGetLines() as line>
        ${line}
  </#list>
  </#if>
  </#if>
  <#if scope.getSet()??>
  <#if scope.getSetLines().size() == 1>
    set: ${scope.getSet()}
  <#else>
    set: |
  <#list scope.getSetLines() as line>
        ${line}
  </#list>
  </#if>
  </#if>
  <#if scope.getRead()??>
  <#if scope.getReadLines().size() == 1>
    read: ${scope.getRead()}
  <#else>
    read: |
  <#list scope.getReadLines() as line>
        ${line}
  </#list>
  </#if>
  </#if>
  <#if scope.getWrite()??>
  <#if scope.getWriteLines().size() == 1>
    read: ${scope.getRead()}
  <#else>
    read: |
  <#list scope.getWriteLines() as line>
          ${line}
  </#list>
  </#if>
  </#if>
</#if>
</#list>
</#if>