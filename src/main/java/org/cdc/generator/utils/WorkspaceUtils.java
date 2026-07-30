package org.cdc.generator.utils;

import net.mcreator.ui.MCreator;

import java.io.File;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/7/21
 */
public class WorkspaceUtils {


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
