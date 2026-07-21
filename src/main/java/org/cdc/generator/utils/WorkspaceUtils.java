package org.cdc.generator.utils;

import net.mcreator.ui.MCreator;
import net.mcreator.workspace.settings.WorkspaceSettings;

import java.io.File;
import java.util.Set;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/7/21
 */
public class WorkspaceUtils {
    public static Set<String> getDependants(WorkspaceSettings workspaceSettings){
        return workspaceSettings.dependants;
    }

    public static File getWorkspaceFolder(MCreator mCreator){
        return mCreator.getWorkspaceFolder();
    }

    public static String weightDependant(int weight){
        return "weight_"+weight;
    }

    public static String supportedVersionDependant(long version){
        return "mcreator" + version;
    }
}
