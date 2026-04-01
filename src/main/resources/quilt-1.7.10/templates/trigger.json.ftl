{
	"dependencies_provided": [
	<#list data.dependencies_provided as dependency>
		{
			  "name": "${dependency.getName()}",
			  "type": "${dependency.getType()}"
		}<#sep>,
	</#list>
	]
	<#if !data.required_apis.isEmpty()>
	,"required_apis": [
	<#list data.required_apis as api>
		"${api}"<#sep>,
	</#list>
	]</#if>
	<#if data.cancelable>	,"cancelable": "true"</#if>
	<#if data.has_result>	,"has_result": "true"</#if>
	<#if data.side != "BOTH">	,"side": "${data.getLowerLetterSide()}"</#if>
}