<#assign weight = "">
{
  "id": "${settings.getModID()}",
  "supportedversions": [
    <#list settings.getDependants() as e>
        <#if e?starts_with("mcreator")>
        ${e?keep_after("mcreator")}<#sep>,
        <#elseif e?starts_with("weight_")>
        <#assign weight = e?keep_after("weight_")>
        </#if>
    </#list>
    ],
  <#if settings.getMCreatorDependenciesRaw().contains("javaplugin")>
  "javaplugin": "${package}.PluginMain",
  </#if>
  "weight": ${weight},
  "info": {
    "name": "${JavaConventions.escapeStringForJava(settings.getModName())}",
    "version": "${settings.getCleanVersion()}",
    "description": "<#if settings.getDescription()?has_content>${JavaConventions.escapeStringForJava(settings.getDescription())}<#else>ExamplePlugin</#if>",
    "author": "<#if settings.getAuthor()?has_content>${JavaConventions.escapeStringForJava(settings.getAuthor())}</#if>"
    <#if settings.getCredits()?has_content>
    ,"credits":"${JavaConventions.escapeStringForJava(settings.getCredits())}"
    </#if>
    <#if settings.getUpdateURL()?has_content>
    ,"updateJSONURL":"${JavaConventions.escapeStringForJava(settings.getUpdateURL())}"
    </#if>
    ,"dependencies": [<#list settings.getRequiredMods() as e>"${e}"<#sep>,</#list>]
    <#if settings.getWebsiteURL()?has_content && settings.getWebsiteURL()?starts_with("https://mcreator.net/plugin/")>
    ,"pluginPageID": ${settings.getWebsiteURL()?keep_after("https://mcreator.net/plugin/")?keep_before("/")}
    </#if>
  }
}