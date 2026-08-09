package org.cdc.generator.utils.decorators;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.mcreator.Launcher;
import net.mcreator.io.FileIO;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.settings.WorkspaceSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdc.generator.utils.WorkspaceUtils;
import org.cdc.generator.utils.ZipUtils;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class WorkspaceDecorator {

    public static final Logger LOG = LogManager.getLogger(WorkspaceDecorator.class);

    private static final HashMap<Workspace,WorkspaceDecorator> workspaceWorkspaceDecoratorHashMap = new HashMap<>();
    public static WorkspaceDecorator getInstance(Workspace workspace){
        if (!workspaceWorkspaceDecoratorHashMap.containsKey(workspace)) {
            workspaceWorkspaceDecoratorHashMap.put(workspace, new WorkspaceDecorator(workspace));
        }
        return workspaceWorkspaceDecoratorHashMap.get(workspace);
    }

    protected Workspace workspace;
    protected JsonObject comment;
    protected File commentFile;

    private WorkspaceDecorator(Workspace workspace) {
        this.workspace = workspace;
        commentFile = new File(workspace.getWorkspaceFolder(), "comment.json");
        if (commentFile.exists()) {
            try {
                this.comment = new Gson().fromJson(new FileReader(commentFile), JsonObject.class);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        if (comment == null){
            comment = new JsonObject();
        }
    }

    public Optional<String> getCommentOfElement(ModElement modElement) {
        var registryName = modElement.getRegistryName();
        if (comment.has(registryName)) {
            var langToComment = comment.get(registryName).getAsJsonObject();
            var lang = PreferencesManager.PREFERENCES.ui.language.get().toLanguageTag();
            var defaultLang = L10N.DEFAULT_LOCALE.toLanguageTag();
            if (langToComment.has(lang)) {
                return Optional.of(langToComment.get(lang).getAsString());
            } else if (langToComment.has(defaultLang)) {
                return Optional.of(langToComment.get(defaultLang).getAsString());
            }
        }
        return Optional.empty();
    }

    public void setComment(ModElement modElement,@Nonnull String commentContent) {
        var registryName = modElement.getRegistryName();
        if (!comment.has(registryName)) {
            comment.add(registryName, new JsonObject());
        }
        var langToComment = comment.get(registryName).getAsJsonObject();
        var lang = PreferencesManager.PREFERENCES.ui.language.get().toLanguageTag();
        langToComment.add(lang,new JsonPrimitive(commentContent));

        try (FileWriter fileWriter = new FileWriter(commentFile)){
            new Gson().toJson(comment,fileWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasWeight(){
        return getDependants().stream()
                .noneMatch(str -> str.startsWith("weight_"));
    }

    public void clearAllWeights(){
        getDependants().stream()
                .filter(str -> str.startsWith("weight_")).forEach(a-> workspace.getWorkspaceSettings().dependants.remove(a));
    }

    public void addWeight(int weight){
        clearAllWeights();
        getWorkspaceSettings().dependants.add(WorkspaceUtils.weightDependant(weight));
    }

    public boolean hasSupportedVersion(long versionLong){
        return getWorkspaceSettings().dependants.stream().anyMatch(a->a.equals(WorkspaceUtils.supportedVersionDependant(versionLong)));
    }

    public void clearAllSupportedVersions(){
        getDependants().stream()
                .filter(str -> str.startsWith("mcreator")).forEach(a-> workspace.getWorkspaceSettings().dependants.remove(a));
    }

    public void addSupportedVersion(long versionLong){
       getWorkspaceSettings().dependants.add(WorkspaceUtils.supportedVersionDependant(versionLong));
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public WorkspaceSettings getWorkspaceSettings(){
        return workspace.getWorkspaceSettings();
    }

    /**
     * This can be used to add weight and supportedversion
     */
    public Set<String> getDependants(){
        return new HashSet<>(workspace.getWorkspaceSettings().dependants);
    }

    public File getWorkspaceFolder(){
        return workspace.getWorkspaceFolder();
    }

    public File getWorkspaceLibraryFile(){
        return new File(getWorkspaceFolder(), ".mcreator/libs");
    }

    public File getWorkspaceRunPluginsFile(){
        return new File(getWorkspaceFolder(), "run/plugins");
    }

    public void reinit(MCreator mcreator){
        mcreator.closeThisMCreator(true);
    }

    public boolean isInDevelopment(){
        return Launcher.version.isDevelopment();
    }

    public void injectMCreatorLibraries(File mcreatorPath){
        var libs = getWorkspaceLibraryFile();
        var oldLibs = new File(getWorkspaceFolder(), "libs");
        if (oldLibs.isDirectory()) {
            FileIO.deleteDir(oldLibs);
        }
        if (libs.isDirectory() && !Launcher.version.isDevelopment()) {
            FileIO.deleteDir(libs);
            LOG.debug("Plugin maker has removed all old jars");
        }

        var mcreatorJar = new File(mcreatorPath,"mcreator.jar");
        var mcreatorExe = new File(mcreatorPath,"mcreator.exe");
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


        var mcreatorLibs = new File(mcreatorPath,"lib");
        if (mcreatorLibs.isDirectory()) {
            FileIO.copyDirectory(mcreatorLibs, libs);
            LOG.debug("Plugin maker has copied all mcreator libs");
        }
    }
}
