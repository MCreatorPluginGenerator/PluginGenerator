<#noparse><#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void on</#noparse>${data.getEventNameUsedAsMethodName()}(${data.eventName} event) {
	    <#list data.getMethodBodyLines() as line>
	    ${line}
	    </#list>
	}