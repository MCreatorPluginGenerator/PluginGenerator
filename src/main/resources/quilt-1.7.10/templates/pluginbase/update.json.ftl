{
    "${settings.getModID()}":{
        "latest": "${settings.getCleanVersion()}",
        "changes": [
            "${settings.getCleanVersion()}"
            <#if settings.getDescription()?has_content>,"${JavaConventions.escapeStringForJava(settings.getDescription())}"</#if>
            <#list data.logs as log>,"${log}"</#list>
        ]
    }
}