It will run when variables write:
```java
@Override public CompoundTag save(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
    <#list variables as var>
        <#if var.getScope().name() == "GLOBAL_WORLD">
            <@var.getType().getScopeDefinition(generator.getWorkspace(), "GLOBAL_WORLD")['write']?interpret/>
        </#if>
    </#list>
    return nbt;
}
```