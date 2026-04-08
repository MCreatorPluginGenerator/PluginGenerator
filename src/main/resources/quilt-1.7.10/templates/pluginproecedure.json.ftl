{
   <#if !data.arg0.isEmpty()>
  "args0": [
       <#list data.arg0 as arg>
       ${arg.toString()}<#sep>,
       </#list>
   ],
  </#if>
  "colour": ${data.getColor()}
  ,"inputsInline": ${data.inputsInline}
  <#if !data.outputs.isEmpty()>
  ,"output": ${data.getOutputs()}
  </#if>
  <#if !data.previousStatement.isEmpty()>
  ,"previousStatement": "${data.previousStatement}"
  </#if>
  <#if !data.nextStatement.isEmpty()>
  ,"nextStatement": "${data.nextStatement}"
  </#if>
    ,"mcreator": {
    <#if !data.toolbox_id.isBlank()>
      "toolbox_id": "${data.toolbox_id}",
    </#if>
    <#if !data.fields.isEmpty()>
      "fields": [
      <#list data.fields as field>
        "${field}"<#sep>,
      </#list>
      ]
    </#if>
    <#if !data.inputs.isEmpty()>
          "inputs": [
          <#list data.inputs as input>
            "${input}"<#sep>,
          </#list>
          ]
    </#if>
    }
}