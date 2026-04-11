<#noparse><#include "procedures.java.ftl">
public ${name}Procedure() {</#noparse>
	${data.eventName}.register(
	    <#list data.getMethodBodyLines() as line>
	    ${line}
	    </#list>
		boolean result = eventResult;
		eventResult = true;
		return result;
	});
}