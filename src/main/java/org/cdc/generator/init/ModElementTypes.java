package org.cdc.generator.init;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;
import org.cdc.generator.elements.*;
import org.cdc.generator.ui.elements.*;
import org.cdc.generator.utils.ioc.Container;

import javax.annotation.Nullable;

@SuppressWarnings("unused") public class ModElementTypes {
    public static final ModElementType<DataListModElement> DATA_LIST = register("plugindatalist", null,
            DataListModElementGUI::new, DataListModElement.class);
    public static final ModElementType<MappingsModElement> MAPPINGS = register("pluginmappings", null,
            MappingsModElementGUI::new, MappingsModElement.class);
    public static final ModElementType<TriggerModElement> TRIGGER = register("plugintrigger", null,
            TriggerModElementGUI::new, TriggerModElement.class);
    public static final ModElementType<VariableModElement> VARIABLE = register("pluginvariable", null,
            VariableModElementGUI::new, VariableModElement.class);
    public static final ModElementType<APIModElement> APIS = register("pluginapis", null, APIModElementGUI::new,
            APIModElement.class);
    public static final ModElementType<TriggerImplementationModElement> TRIGGER_IMPL = register("plugintriggerimpl",
            null, TriggerImplementationModElementGUI::new, TriggerImplementationModElement.class);
    public static final ModElementType<VariableImplementationModElement> VARIABLE_IMPL = register("pluginvariableimpl",
            null, VariableImplementationModElementGUI::new, VariableImplementationModElement.class);
    public static final ModElementType<ProcedureCategoryModElement> PROCEDURE_CATEGORY = register(
            "pluginprocedurecategory", null, ProcedureCategoryModElementGUI::new, ProcedureCategoryModElement.class);
    public static final ModElementType<PluginProcedureModElement> PROCEDURE = register("pluginprocedure", null,
            PluginProceduresModElementGUI::new, PluginProcedureModElement.class);
    public static final ModElementType<PluginProcedureImplementationModElement> PROCEDURE_IMPLEMENTATION = register(
            "pluginprocedureimpl", null, PluginProcedureImplementationModElementGUI::new,
            PluginProcedureImplementationModElement.class);
    public static final ModElementType<PluginCmdArgsCategoryModElement> CMD_ARGS_CATEGORY = register(
            "plugincmdargscategory", null, PluginCmdArgsCategoryModElementGUI::new,
            PluginCmdArgsCategoryModElement.class);
    public static final ModElementType<PluginAITasksCategoryModElement> AI_TASKS_CATEGORY = register(
            "pluginaitaskcategory", null, PluginAITasksCategoryModElementGUI::new,
            PluginAITasksCategoryModElement.class);
    public static final ModElementType<PluginCmdArgsProcedureModElement> CMD_ARGS_PROCEDURE = register(
            "plugincmdargsprocedure", null, PluginCmdArgsProcedureModElementGUI::new,
            PluginCmdArgsProcedureModElement.class);

    public static final ModElementType<UpdateJsonModElement> UPDATE_JSON_MOD_ELEMENT_MOD_ELEMENT_TYPE = register(
            "updatelogs", null, UpdateLogJsonModElementGUI::new, UpdateJsonModElement.class);

    public static final ModElementType<PluginAITasksProcedureModElement> AI_TASK_PROCEDURE_MOD_ELEMENT_GUI_MOD_ELEMENT_TYPE = register(
            "pluginaitasksprocedure", null, PluginAITaskProcedureModElementGUI::new,
            PluginAITasksProcedureModElement.class);

    private static <E extends GeneratableElement> ModElementType<E> register(String registryName,
            @Nullable Character shortcut, ModElementType.ModElementGUIProvider<E> modElementGUIProvider,
            Class<E> modElementStorageClass) {
        var modElementType = new ModElementType<>(registryName, shortcut, (mcreator, modElement, editingMode) -> {
            var obj = Container.getInstance().inject(modElementGUIProvider.get(mcreator, modElement, editingMode));
            if (obj instanceof AbstractConfigurationTableModElementGUI<E> elementGUI) {
                elementGUI.initAfterAll();
            }
            return obj;
        }, modElementStorageClass);
        ModElementTypeLoader.register(modElementType);
        return modElementType;
    }
}
