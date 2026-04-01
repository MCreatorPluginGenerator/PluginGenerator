<#noparse><#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void</#noparse> on${data.getEventNameUsedAsMethodName()}(${data.eventName} event) {
	    <#list data.getMethodBodyLines() as line>
	    ${line}
	    </#list>
	}