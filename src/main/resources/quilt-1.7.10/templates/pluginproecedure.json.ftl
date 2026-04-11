{
  <#if !data.arg0.isEmpty()>
  "args0": [
       <#list data.arg0 as arg>
       ${arg.getArg0Json().toString()}<#sep>,
       </#list>
   ],
  </#if>
  "colour": ${data.getColor()}
  ,"inputsInline": ${data.inputsInline}
  <#if data.getOutputs()??>
  ,"output": ${data.getOutputs()}
  </#if>
  <#if !data.getExtensions().isEmpty()>
  ,"extensions": [
     <#list data.extensions as extension>
       ${extension}<#sep>,
     </#list>
  ]
  </#if>
  <#if !data.previousStatement.isEmpty()>
  ,"previousStatement": "${data.previousStatement}"
  </#if>
  <#if !data.nextStatement.isEmpty()>
  ,"nextStatement": "${data.nextStatement}"
  </#if>
   ,"mcreator": {
       "toolbox_id": "${data.toolbox_id}"
       <#if !data.group.isBlank()>
       ,"group": "${data.group}"
       </#if>
       <#if !data.toolbox_init.isEmpty()>
           ,"toolbox_init": [
                <#list data.toolbox_init as init>
                    ${init}<#sep>,
                </#list>
           ],
       </#if>
       <#if !data.statements.isEmpty()>
         ,"statements": [
            <#list data.statements as statement>
            {
                "name": "${statement}"
            }<#sep>,
            </#list>
         ]
       </#if>
       <#if !data.fields.isEmpty()>
         ,"fields": [
         <#list data.fields as field>
           "${field}"<#sep>,
         </#list>
         ]
       </#if>
       <#if !data.inputs.isEmpty()>
         ,"inputs": [
             <#list data.inputs as input>
               "${input}"<#sep>,
             </#list>
         ]
       </#if>
       <#if !data.dependencies.isEmpty()>
        ,"dependencies": [
             <#list data.dependencies as dependency>
             {
               "name": "${dependency.getName()}",
               "type": "${dependency.getType()}"
             }<#sep>,
             </#list>
        ]
       </#if>
       <#if !data.warnings.isEmpty()>
           ,"warnings": [
             <#list data.warnings as warning>
                ${warning}<#sep>,
             </#list>
           ]
       </#if>
       <#if !data.required_apis.isEmpty()>
         ,"required_apis": [
           <#list data.required_apis as api>
            ${api}<#sep>,
           </#list>
         ]
       </#if>
   }
}