package org.cdc.generator;

import net.mcreator.Launcher;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.JavaPlugin;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.events.ApplicationLoadedEvent;
import net.mcreator.plugin.events.ModifyTemplateResultEvent;
import net.mcreator.plugin.events.PreGeneratorsLoadingEvent;
import net.mcreator.plugin.events.ui.BlocklyPanelRegisterDOMData;
import net.mcreator.plugin.events.workspace.MCreatorLoadedEvent;
import net.mcreator.plugin.events.workspace.WorkspaceBuildStartedEvent;
import net.mcreator.plugin.events.workspace.WorkspaceTaskFinishedEvent;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdc.generator.elements.interfaces.IUniqueElement;
import org.cdc.generator.init.Menus;
import org.cdc.generator.init.ResourcePanels;
import org.cdc.generator.ui.preferences.PluginMakerPreference;
import org.cdc.generator.utils.DialogUtils;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.ZipUtils;
import org.cdc.generator.utils.ioc.Container;
import org.cdc.generator.utils.writers.JSONWriter;
import org.cdc.generator.utils.writers.YamlWriter;
import org.cdc.js.JavaScriptBridge;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PluginMain extends JavaPlugin {
    public static final Logger LOG = LogManager.getLogger("PluginMaker");

    private static PluginMain INSTANCE = null;

    public static PluginMain getINSTANCE() {
        return INSTANCE;
    }

    private MCreatorApplication application;

    public PluginMain(Plugin plugin) {
        super(plugin);

        INSTANCE = this;

        Container.getInstance().registerObject("pluginMain", () -> INSTANCE);

        addListener(MCreatorLoadedEvent.class, this::initPluginMakerWorkspace);

        addListener(PreGeneratorsLoadingEvent.class, event -> {
            try {
                Class.forName("org.cdc.generator.init.ModElementTypes");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        addListener(WorkspaceBuildStartedEvent.class, event -> {
            if (Utils.isNotPluginGenerator(event.getMCreator().getGenerator())) {
                return;
            }
            FileIO.removeEmptyDirs(event.getMCreator().getGenerator().getModAssetsRoot());
        });

        addListener(WorkspaceTaskFinishedEvent.TaskCompleted.class, event -> {
            var mcreator = event.getMCreator();
            var hashMap = new HashMap<String, ArrayList<String>>();
            var elements = new ArrayList<ModElement>();
            for (ModElement modElement : mcreator.getWorkspace().getModElements()) {
                if (modElement.getGeneratableElement() instanceof IUniqueElement uniqueElement) {
                    hashMap.compute("duplicated " + uniqueElement.getUniqueID(), (a, b) -> {
                        if (b == null) {
                            b = new ArrayList<>();
                        }
                        b.add(modElement.getName());
                        return b;
                    });
                }

                if (modElement.getAssociatedFiles().stream().anyMatch(a -> !a.exists())) {
                    modElement.setCompiles(false);
                    elements.add(modElement);
                }
            }
            for (Map.Entry<String, ArrayList<String>> stringArrayListEntry : new HashSet<>(hashMap.entrySet())) {
                if (stringArrayListEntry.getValue().size() == 1) {
                    hashMap.remove(stringArrayListEntry.getKey());
                }
            }
            if (Utils.isNotPluginGenerator(mcreator.getGenerator())) {
                return;
            }
            if (!hashMap.isEmpty()) {
                mcreator.getGradleConsole().appendPlainText("Duplicated elements: ", Color.RED);
                mcreator.getGradleConsole().appendPlainText(
                        hashMap.entrySet().stream().map(Object::toString).collect(Collectors.joining("\n")), Color.RED);

                for (Map.Entry<String, ArrayList<String>> stringArrayListEntry : hashMap.entrySet()) {
                    for (String s : stringArrayListEntry.getValue()) {
                        var element = mcreator.getWorkspace().getModElementByName(s);
                        element.setCompiles(false);
                        elements.add(element);
                    }
                }
            }

            if (!elements.isEmpty()) {
                mcreator.getGradleConsole().append("");
                mcreator.getGradleConsole().appendPlainText(
                        "If you find this line, you should know the fact that your workspace may not generate some elements or has duplicated elements.",
                        Color.BLUE);
                CompletableFuture.runAsync(() -> {
                    DialogUtils.showErrorElementDialog(mcreator, elements);
                });
            }
        });

        addListener(ApplicationLoadedEvent.class, event -> {
            application = event.getMCreatorApplication();
            PluginMakerPreference.INSTANCE = new PluginMakerPreference("plugin_generator");
            Container.getInstance().registerObject("preferences", () -> PluginMakerPreference.INSTANCE);
        });

        addListener(BlocklyPanelRegisterDOMData.class, a -> {
            a.addJavaScriptBridge("plugingenerator", new JavaScriptBridge());
        });

        addListener(ModifyTemplateResultEvent.class, event -> {
            if (event.getTemplateName().endsWith("yaml.ftl")) {
                event.setTemplateOutput(YamlWriter.INSTANCE.formatString(event.getTemplateOutputOriginal()));
            } else if (event.getTemplateName().equals("pluginproecedure.json.ftl")){
                event.setTemplateOutput(JSONWriter.INSTANCE.formatString(event.getTemplateOutputOriginal()));
            }
        });

        Menus.registerMenuVisibleControls(this);
    }

    private void initPluginMakerWorkspace(MCreatorLoadedEvent event) {
        var mcreator = event.getMCreator();

        if (Utils.isNotPluginGenerator(mcreator.getGenerator())) {
            LOG.debug("{} is not plugin maker", mcreator.getGenerator().getGeneratorName());
            return;
        }

        registerAll(mcreator);

        CompletableFuture.runAsync(() -> {
            if (mcreator.getWorkspaceSettings().dependants.stream().noneMatch(str -> str.startsWith("weight_"))) {
                LOG.debug("Try to add weight_0 to dependants");
                mcreator.getWorkspaceSettings().dependants.add("weight_0");

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null,
                            "But for the help from community, this will be not finished. If you encounter a bug, please report.");
                });
            }

            var libs = new File(mcreator.getWorkspaceFolder(), ".mcreator/libs");
            var oldLibs = new File(mcreator.getWorkspaceFolder(), "libs");
            if (oldLibs.isDirectory()) {
                FileIO.deleteDir(oldLibs);
            }
            if (libs.isDirectory() && !Launcher.version.isDevelopment()) {
                FileIO.deleteDir(libs);
                LOG.debug("Plugin maker has removed all old jars");
            }

            var mcreatorJar = new File("mcreator.jar");
            var mcreatorExe = new File("mcreator.exe");
            var mcreatorLibJar = new File(libs, "mcreator.jar");
            if (mcreatorJar.isFile()) {
                FileIO.copyFile(mcreatorJar, mcreatorLibJar);
                LOG.debug("Plugin maker has copied main mcreator lib, type: jar");
            } else if (mcreatorExe.isFile()) {
                try {
                    var pureMCreatorJar = ZipUtils.tryToConvertExeToJar(mcreatorExe);
                    FileIO.copyFile(pureMCreatorJar, mcreatorJar);
                    FileIO.copyFile(pureMCreatorJar, mcreatorLibJar);
                    LOG.debug("Plugin maker has copied main mcreator libs, type: exe");
                    Files.deleteIfExists(pureMCreatorJar.toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            var mcreatorLibs = new File("lib");
            if (mcreatorLibs.isDirectory()) {
                FileIO.copyDirectory(mcreatorLibs, libs);
                LOG.debug("Plugin maker has copied all mcreator libs");
            }

            if (!Launcher.version.isDevelopment()) {
                var runPlugins = new File(mcreator.getWorkspaceFolder(), "run/plugins");
                if (runPlugins.isDirectory()) {
                    FileIO.deleteDir(runPlugins);
                }

                var mcreatorPlugins = new File("plugins");
                if (mcreatorPlugins.isDirectory()) {
                    FileIO.copyDirectory(mcreatorPlugins, runPlugins);
                    LOG.debug("Plugin maker has copied all mcreator plugins");
                }
            }
        });
    }

    public void registerAll(MCreator mcreator) {
        ResourcePanels.register(mcreator);
        Menus.registerAllMenus(mcreator);
        Menus.registerAllSubMenus(mcreator);
    }

    public MCreatorApplication getApplication() {
        return application;
    }
}
