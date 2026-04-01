name: "${data.apiName}"
<#list data.configurations as configuration>
${configuration.getGenerator()}:
  required_when_enabled: ${configuration.isRequiredWhenEnable()}<#if configuration.getVersionRange()??>
  versionRange: "${configuration.getVersionRange()}"</#if><#if !configuration.getUpdateFiles().isEmpty()>
  update_files: <#list configuration.getUpdateFiles() as file>
    - "${file}"</#list></#if>
  gradle:
<#list configuration.getYamlGradle() as line>
    ${line}
</#list>
</#list>