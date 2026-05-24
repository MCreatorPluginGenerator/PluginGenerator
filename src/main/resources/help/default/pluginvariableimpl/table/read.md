in neoforge1.21.1/modbase/variableslist.java.ftl:
```java
public void read(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
    <#list variables as var>
        <#if var.getScope().name() == "GLOBAL_WORLD">
            <@var.getType()
            .getScopeDefinition(generator.getWorkspace(), "GLOBAL_WORLD")
            ['read']?interpret/>
        </#if>
    </#list>
}
```