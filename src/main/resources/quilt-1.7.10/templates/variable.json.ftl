{
  "color": ${data.getFormattedColor()},
  "blocklyVariableType": "${data.blocklyVariableType}"
	<#if data.ignoredByCoverage>,"ignoredByCoverage": true</#if>
	<#if data.nullable>,"nullable": true</#if>
	<#if !data.required_apis.isEmpty()>
	,"required_apis": [
	<#list data.required_apis as api>
		"${api}"<#sep>,
	</#list>
	]</#if>
}