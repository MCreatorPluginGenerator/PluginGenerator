{
  "color": ${data.getColor()}
  <#if data.getParentCategory() != "None">
  ,"parent_category": "${data.getParentCategory()}"
  </#if>
  <#if data.api>
  ,"api": true
  </#if>
}