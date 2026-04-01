package org.cdc.generator.init;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.workspace.WorkspacePanel;
import org.cdc.generator.ui.ResourcePanelCorePack;
import org.cdc.generator.ui.ResourcePanelModTypes;
import org.cdc.generator.ui.ResourcePanelTemplates;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ResourcePanels {

    static {
        nameToPanels = new HashMap<>();
        register("mod_types", ResourcePanelModTypes::new);
        register("user_templates", ResourcePanelTemplates::new);
        register("core_pack", ResourcePanelCorePack::new);
    }

    private static final Map<String, Function<WorkspacePanel, JPanel>> nameToPanels;

    private static void register(String name, Function<WorkspacePanel, JPanel> resourcePanel) {
        nameToPanels.put(name, resourcePanel);

    }

    public static void register(MCreator mCreator) {
        if (mCreator.getWorkspacePanel() instanceof WorkspacePanel modMaker) {
            nameToPanels.forEach((a, b) -> {
                modMaker.resourcesPan.addResourcesTab(L10N.t("workspace.resources.tab." + a), b.apply(modMaker));
            });
        }
    }
}
