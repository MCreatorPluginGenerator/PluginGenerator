package org.cdc.generator;

import com.google.gson.JsonObject;
import net.mcreator.Launcher;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.DynamicURLClassLoader;
import net.mcreator.plugin.JavaPlugin;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.plugin.events.ApplicationLoadedEvent;
import net.mcreator.plugin.events.ModifyTemplateResultEvent;
import net.mcreator.plugin.events.PreGeneratorsLoadingEvent;
import net.mcreator.plugin.events.ui.BlocklyPanelRegisterDOMData;
import net.mcreator.plugin.events.ui.ModElementGUIEvent;
import net.mcreator.plugin.events.ui.TabEvent;
import net.mcreator.plugin.events.workspace.MCreatorLoadedEvent;
import net.mcreator.plugin.events.workspace.WorkspaceBuildStartedEvent;
import net.mcreator.plugin.events.workspace.WorkspaceTaskFinishedEvent;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdc.framework.utils.L10NHelper;
import org.cdc.generator.elements.PluginProcedureModElement;
import org.cdc.generator.elements.interfaces.IUniqueElement;
import org.cdc.generator.init.Menus;
import org.cdc.generator.init.ResourcePanels;
import org.cdc.generator.ui.InformationDock;
import org.cdc.generator.ui.preferences.PluginMakerPreference;
import org.cdc.generator.utils.FTLUtils;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.WorkspaceUtils;
import org.cdc.generator.utils.ZipUtils;
import org.cdc.generator.utils.ioc.Container;
import org.cdc.generator.utils.writers.JSONWriter;
import org.cdc.generator.utils.writers.YamlWriter;
import org.cdc.js.JavaScriptBridge;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PluginMain extends JavaPlugin {
    public static final Logger LOG = LogManager.getLogger("PluginMaker");

    private static PluginMain INSTANCE = null;

    public static PluginMain getINSTANCE() {
        return INSTANCE;
    }

    private MCreatorApplication application;

    private ClassLoader dependsClassLoader;

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
            var emptyToolbox = new ArrayList<>();
            ArrayList<ModElement> notGenerate = new ArrayList<>();
            HashMap<String, ArrayList<String>> duplicatedElements = new HashMap<>();
            for (ModElement modElement : mcreator.getWorkspace().getModElements()) {
                if (modElement.getGeneratableElement() instanceof IUniqueElement uniqueElement) {
                    duplicatedElements.compute("duplicated " + uniqueElement.getUniqueID(), (a, b) -> {
                        if (b == null) {
                            b = new ArrayList<>();
                        }
                        b.add(modElement.getName());
                        return b;
                    });
                }

                if (modElement.getGeneratableElement() instanceof PluginProcedureModElement pluginProcedureModElement) {
                    if (pluginProcedureModElement.toolbox_init.isEmpty()) {
                        emptyToolbox.add(pluginProcedureModElement.getModElement().getName());
                    }
                }

                if (modElement.getAssociatedFiles().stream().anyMatch(a -> !a.exists())) {
                    modElement.setCompiles(false);
                    notGenerate.add(modElement);
                }
            }
            for (Map.Entry<String, ArrayList<String>> stringArrayListEntry : new HashSet<>(
                    duplicatedElements.entrySet())) {
                if (stringArrayListEntry.getValue().size() == 1) {
                    duplicatedElements.remove(stringArrayListEntry.getKey());
                }
            }
            if (Utils.isNotPluginGenerator(mcreator.getGenerator())) {
                return;
            }
            if (!emptyToolbox.isEmpty()) {
                mcreator.getGradleConsole()
                        .appendPlainText("Warning: empty toolbox init " + emptyToolbox, Color.YELLOW);
            }
            if (!duplicatedElements.isEmpty()) {
                mcreator.getGradleConsole().appendPlainText(
                        duplicatedElements.entrySet().stream().map(Object::toString).collect(Collectors.joining("\n")),
                        Color.RED);

                for (Map.Entry<String, ArrayList<String>> stringArrayListEntry : duplicatedElements.entrySet()) {
                    for (String s : stringArrayListEntry.getValue()) {
                        var element = mcreator.getWorkspace().getModElementByName(s);
                        element.setCompiles(false);
                        notGenerate.add(element);
                    }
                }
            }

            if (!notGenerate.isEmpty()) {
                mcreator.getGradleConsole().append("");
                mcreator.getGradleConsole().appendPlainText("some elements didn't generate properly.", Color.BLUE);
                dockHashMap.get(mcreator).getDuplicatedElements().putAll(duplicatedElements);
                dockHashMap.get(mcreator).getNotGenerate().addAll(notGenerate);
                mcreator.getLeftDockRegion().setDockVisibility("information_dock", true);
            }

            dockHashMap.get(mcreator).reloadTree();
        });

        addListener(ApplicationLoadedEvent.class, event -> {
            application = event.getMCreatorApplication();
            PluginMakerPreference.INSTANCE = new PluginMakerPreference("plugin_generator");
            Container.getInstance().registerObject("preferences", () -> PluginMakerPreference.INSTANCE);
            try {
                dependsClassLoader = loadAllDepends();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });

        addListener(BlocklyPanelRegisterDOMData.class, a -> {
            a.addJavaScriptBridge("plugingenerator", new JavaScriptBridge());
        });

        addListener(ModifyTemplateResultEvent.class, event -> {
            if (event.getTemplateName() == null) {
                return;
            }
            if (event.getTemplateName().endsWith("yaml.ftl")) {
                event.setTemplateOutput(YamlWriter.INSTANCE.formatString(event.getTemplateOutputOriginal()));
            } else if (event.getTemplateName().equals("pluginproecedure.json.ftl")) {
                event.setTemplateOutput(JSONWriter.INSTANCE.formatString(event.getTemplateOutputOriginal()));
            } else if (event.getTemplateName().endsWith("java.ftl")
                    && PluginMakerPreference.INSTANCE.generateFtlComment.get()) {
                // head
                String comment = event.getTemplateName();
                LOG.info("{}:{}", event.getTemplateName(), event.getDataModel());
                StringBuilder builder = new StringBuilder();
                if (FTLUtils.isCombineCode(event.getDataModel())) {
                    var jsonO = new JsonObject();
                    jsonO.addProperty("fileName", comment);
                    jsonO.addProperty("localization", L10N.t(L10NHelper.getProcedureKey(
                            FTLUtils.getFileNameWithoutExtensions(event.getTemplateName()))));
                    comment = jsonO.toString();
                } else if (FTLUtils.isTriggerCode(event.getDataModel())) {
                    var jsonO = new JsonObject();
                    jsonO.addProperty("fileName", comment);
                    jsonO.addProperty("localization", L10N.t(L10NHelper.getTriggerKey(
                            FTLUtils.getFileNameWithoutExtensions(event.getTemplateName()))));
                    comment = jsonO.toString();
                } else if (FTLUtils.isClass(event.getTemplateOutputOriginal())) {
                    builder.append("""
                            /*
                            Expected answer format:
                            - FTL template: xxx.java.ftl
                            - Localization: “xxxxxx”
                            - Brief reason: xxx
                            If user expect you to fix a logic problem. You can use the template more than one time to exchange template:
                            - FLT template:
                            - Localization1:
                            - Replaced FTL template:
                            - Localization2:
                            - Brief reason:
                            */
                            """);
                }
                if (FTLUtils.isInputProcedure(event.getTemplateOutputOriginal())) {
                    builder.append(FTLUtils.generateCodeHead(event.getTemplateOutput(), "/* head " + comment + " */"))
                            .append("/* tail ").append(event.getTemplateName()).append(" */");
                } else {
                    builder.append("/* head ").append(comment).append(" */").append(event.getTemplateOutput())
                            .append("/* tail ").append(event.getTemplateName()).append(" */");
                }
                event.setTemplateOutput(builder.toString());
            }
        });

        this.addListener(TabEvent.Shown.class, event -> {
            MCreator mcreator = null;
            if (event.getTab().getContent() instanceof ModElementGUI<?> modElementGUI) {
                mcreator = modElementGUI.getMCreator();
            }
            if (event.getTab().getContent() instanceof WorkspacePanel workspacePanel) {
                mcreator = workspacePanel.getMCreator();
            }
            if (dockHashMap.containsKey(mcreator)) {
                dockHashMap.get(mcreator).reloadTree();
            }

        });

        this.addListener(ModElementGUIEvent.WhenSaving.class, event -> {
            dockHashMap.get(event.getMCreator()).getNotGenerate();
            dockHashMap.get(event.getMCreator()).getDuplicatedElements();
        });

        Menus.registerMenuVisibleControls(this);
    }

    private final HashMap<MCreator, InformationDock> dockHashMap = new HashMap<>();

    private void initPluginMakerWorkspace(MCreatorLoadedEvent event) {
        var mcreator = event.getMCreator();

        var informationDock = new InformationDock(mcreator);
        dockHashMap.put(mcreator, informationDock);

        mcreator.getLeftDockRegion()
                .addDock("information_dock", 380, "Information", UIRES.get("16px.search"), informationDock);

        if (Utils.isNotPluginGenerator(mcreator.getGenerator())) {
            LOG.debug("{} is not plugin maker", mcreator.getGenerator().getGeneratorName());
            return;
        }

        informationDock.setMcreatorPluginsWorkspace(true);
        informationDock.reloadTree();

        registerAll(mcreator);

        CompletableFuture.runAsync(() -> {
            if (WorkspaceUtils.getDependants(mcreator.getWorkspaceSettings()).stream()
                    .noneMatch(str -> str.startsWith("weight_"))) {
                LOG.debug("Try to add weight_0 to dependants");
                WorkspaceUtils.getDependants(mcreator.getWorkspaceSettings()).add(WorkspaceUtils.weightDependant(0));

                warnSnapshot();
            }

            var libs = new File(WorkspaceUtils.getWorkspaceFolder(mcreator), ".mcreator/libs");
            var oldLibs = new File(WorkspaceUtils.getWorkspaceFolder(mcreator), "libs");
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

                for (Plugin instancePlugin : PluginLoader.INSTANCE.getPlugins()) {
                    try {
                        Files.copy(instancePlugin.getFile().toPath(), runPlugins.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private void warnSnapshot() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                    "This wouldn't have been possible without the incredible support from the community. If you encounter any bugs, please report them on my plugin page.",
                    "You are using snapshot", JOptionPane.WARNING_MESSAGE);
        });
    }

    private ClassLoader loadAllDepends() throws MalformedURLException {
        Vector<URL> urls = new Vector<>();
        for (Plugin instancePlugin : PluginLoader.INSTANCE.getPlugins()) {
            if (!instancePlugin.equals(this.plugin)) {
                urls.add(instancePlugin.toURL());
            }
        }
        return new DynamicURLClassLoader(urls.toArray(new URL[0]), PluginMain.class.getClassLoader());
    }

    private void registerAll(MCreator mcreator) {
        ResourcePanels.register(mcreator);
        Menus.registerAllMenus(mcreator);
        Menus.registerAllSubMenus(mcreator);
    }

    public MCreatorApplication getApplication() {
        return application;
    }

    public ClassLoader getDependsClassLoader() {
        return dependsClassLoader;
    }
}