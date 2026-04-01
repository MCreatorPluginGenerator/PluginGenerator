package org.cdc.generator.init;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.workspace.elements.ModElement;
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

    private static <E extends GeneratableElement> ModElementType<E> register(String registryName,
            @Nullable Character shortcut, ModElementType.ModElementGUIProvider<E> modElementGUIProvider,
            Class<E> modElementStorageClass) {
        var modElementType = new ModElementType<>(registryName, shortcut,
                (mcreator, modElement, editingMode) -> Container.getInstance().inject(modElementGUIProvider.get(mcreator, modElement, editingMode)),
                modElementStorageClass);
        ModElementTypeLoader.register(modElementType);
        return modElementType;
    }
}
